package pl.poznan.put.structure.tertiary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;

import pl.poznan.put.pdb.PdbParsingException;
import pl.poznan.put.pdb.analysis.PdbModel;
import pl.poznan.put.pdb.analysis.PdbParser;

/**
 * A common manager of loaded PDB files shared between all classes.
 *
 * @author tzok
 */
public final class StructureManager {
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final List<StructureInfo> STRUCTURES = new ArrayList<StructureInfo>();
    private static final PdbParser PDB_READER = new PdbParser(false);

    public static List<PdbModel> getAllStructures() {
        List<PdbModel> result = new ArrayList<PdbModel>();
        for (StructureInfo si : StructureManager.STRUCTURES) {
            result.add(si.getStructure());
        }
        return result;
    }

    public static List<String> getAllNames() {
        List<String> result = new ArrayList<String>();
        for (StructureInfo si : StructureManager.STRUCTURES) {
            result.add(si.getName());
        }
        return result;
    }

    public static File getFile(PdbModel structure) {
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getStructure().equals(structure)) {
                return si.getPath();
            }
        }
        throw new IllegalArgumentException("Failed to find PdbModel");
    }

    public static PdbModel getStructure(String name) {
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getName().equals(name)) {
                return si.getStructure();
            }
        }
        throw new IllegalArgumentException("Failed to find PdbModel");
    }

    public static String getName(PdbModel structure) {
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getStructure().equals(structure)) {
                return si.getName();
            }
        }
        throw new IllegalArgumentException("Failed to find PdbModel");
    }

    public static List<PdbModel> getModels(File file) {
        List<PdbModel> result = new ArrayList<PdbModel>();
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getPath().equals(file)) {
                result.add(si.getStructure());
            }
        }
        return result;
    }

    public static List<String> getNames(List<PdbModel> structures) {
        List<String> result = new ArrayList<String>();
        for (PdbModel s : structures) {
            result.add(StructureManager.getName(s));
        }
        return result;
    }

    /**
     * Load a structure and remember it being already cached.
     *
     * @param path
     *            Path to the PDB file.
     * @return Structure object..
     * @throws IOException
     * @throws PdbParsingException
     */
    public static List<PdbModel> loadStructure(File file) throws IOException, PdbParsingException {
        List<PdbModel> models = StructureManager.getModels(file);
        if (models.size() > 0) {
            return models;
        }

        String fileContent = StructureManager.readFileUnzipIfNeeded(file);
        String name = file.getName();

        if (name.endsWith(".cif") || name.endsWith(".cif.gz")) {
            if (!StructureManager.isMmCif(fileContent)) {
                throw new IOException("File is not a mmCIF structure: " + file);
            }
            // TODO: Implement a parser for mmCIF format
            throw new UnsupportedOperationException("Sorry, mmCIF parsing is currently unavailable");
        }

        if (!StructureManager.isPdb(fileContent)) {
            throw new IOException("File is not a PDB structure: " + file);
        }

        List<PdbModel> structures = StructureManager.PDB_READER.parse(fileContent);
        StructureManager.storeStructureInfo(file, structures);
        return structures;
    }

    private static String readFileUnzipIfNeeded(File file) throws IOException {
        ByteArrayOutputStream copyStream = null;
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
            copyStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, copyStream);
            byte[] byteArray = copyStream.toByteArray();

            if (StructureManager.isGzipStream(byteArray)) {
                return StructureManager.unzipContent(byteArray);
            }

            return copyStream.toString(StructureManager.ENCODING_UTF_8);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(copyStream);
        }
    }

    private static String unzipContent(byte[] byteArray) throws IOException {
        ByteArrayInputStream inputStream = null;
        GZIPInputStream gzipInputStream = null;

        try {
            inputStream = new ByteArrayInputStream(byteArray);
            gzipInputStream = new GZIPInputStream(inputStream);
            return IOUtils.toString(gzipInputStream);
        } finally {
            IOUtils.closeQuietly(gzipInputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static boolean isGzipStream(byte[] bytes) {
        if (bytes.length < 2) {
            return false;
        }

        int head = bytes[0] & 0xff | bytes[1] << 8 & 0xff00;
        return GZIPInputStream.GZIP_MAGIC == head;
    }

    public static List<PdbModel> loadStructure(String pdbId) throws IOException, PdbParsingException {
        InputStream stream = null;

        try {
            URL url = new URL("http://www.rcsb.org/pdb/download/downloadFile.do?fileFormat=pdb&compression=NO&structureId=" + pdbId);
            stream = url.openStream();
            String pdbContent = IOUtils.toString(stream, StructureManager.ENCODING_UTF_8);

            File pdbFile = File.createTempFile("mcq", ".pdb");
            FileUtils.writeStringToFile(pdbFile, pdbContent, StructureManager.ENCODING_UTF_8);

            List<PdbModel> models = StructureManager.PDB_READER.parse(pdbContent);
            StructureManager.storeStructureInfo(pdbFile, models);
            return models;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public static void remove(File path) {
        List<Integer> toRemove = new ArrayList<Integer>();

        for (int i = 0; i < StructureManager.STRUCTURES.size(); i++) {
            StructureInfo si = StructureManager.STRUCTURES.get(i);
            if (si.getPath().equals(path)) {
                toRemove.add(i);
            }
        }

        for (int i : toRemove) {
            StructureManager.STRUCTURES.remove(i);
        }
    }

    private static boolean isMmCif(String fileContent) {
        return fileContent.startsWith("data_");
    }

    private static boolean isPdb(String fileContent) {
        Pattern pdbPattern = Pattern.compile("^ATOM", Pattern.MULTILINE);
        Matcher matcher = pdbPattern.matcher(fileContent);
        return matcher.find();
    }

    private static void storeStructureInfo(File file, List<PdbModel> structures) {
        String format = "%s";

        if (structures.size() > 1) {
            int count = structures.size();
            int order = 10;
            int leading = 1;
            while (order < count) {
                leading++;
                order *= 10;
            }
            format = "%s.%0" + leading + "d";
        }

        for (int i = 0; i < structures.size(); i++) {
            PdbModel model = structures.get(i);
            String name = model.getIdCode();

            if (StringUtils.isBlank(name)) {
                name = file.getName();
                if (name.endsWith(".pdb") || name.endsWith(".cif")) {
                    name = name.substring(0, name.length() - 4);
                } else if (name.endsWith(".pdb.gz") || name.endsWith(".cif.gz")) {
                    name = name.substring(0, name.length() - 7);
                }
            }

            StructureManager.STRUCTURES.add(new StructureInfo(model, file, String.format(format, name, i + 1)));
        }
    }

    private StructureManager() {
    }
}
