package pl.poznan.put.structure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.bio.structure.io.MMCIFFileReader;
import org.biojava.bio.structure.io.PDBFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A common manager of loaded PDB files shared between all classes.
 * 
 * @author tzok
 */
public final class StructureManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(StructureManager.class);

    private static final List<StructureInfo> STRUCTURES = new ArrayList<StructureInfo>();
    private static final MMCIFFileReader MMCIF_READER = new MMCIFFileReader();
    private static final PDBFileReader PDB_READER = new PDBFileReader();
    private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
    private static final File PDB_DIR = new File(StructureManager.TMP_DIR, "pdbs");

    static {
        // AbstractUserArgumentProcessor.PDB_DIR is what PDBFileReader in
        // BioJava checks!
        StructureManager.PDB_DIR.mkdirs();
        System.setProperty(AbstractUserArgumentProcessor.PDB_DIR, StructureManager.PDB_DIR.getAbsolutePath());
    }

    public static List<Structure> getAllStructures() {
        List<Structure> result = new ArrayList<Structure>();
        for (StructureInfo si : StructureManager.STRUCTURES) {
            result.add(si.getStructure());
        }
        return result;
    }

    public static File getFile(Structure structure) {
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getStructure().equals(structure)) {
                return si.getPath();
            }
        }
        return null;
    }

    public static String getName(Structure structure) {
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getStructure().equals(structure)) {
                return si.getName();
            }
        }
        return null;
    }

    public static List<Structure> getModels(File file) {
        List<Structure> result = new ArrayList<Structure>();
        for (StructureInfo si : StructureManager.STRUCTURES) {
            if (si.getPath().equals(file)) {
                result.add(si.getStructure());
            }
        }
        return result;
    }

    public static List<String> getNames(List<Structure> structures) {
        List<String> result = new ArrayList<String>();
        for (Structure s : structures) {
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
     */
    public static List<Structure> loadStructure(File file) throws IOException {
        List<Structure> models = StructureManager.getModels(file);
        if (models.size() > 0) {
            return models;
        }

        try {
            Structure structure;
            String name = file.getName();

            if (name.endsWith(".cif") || name.endsWith(".cif.gz")) {
                if (!StructureManager.isMmCif(file)) {
                    throw new IOException("File is not a mmCIF structure: " + file);
                }
                structure = StructureManager.MMCIF_READER.getStructure(file);
            } else {
                if (!StructureManager.isPdb(file)) {
                    throw new IOException("File is not a PDB structure: " + file);
                }
                structure = StructureManager.PDB_READER.getStructure(file);
            }

            return StructureManager.storeStructureInfo(file, structure);
        } catch (IOException e) {
            String message = "Failed to load structure: " + file;
            StructureManager.LOGGER.error(message, e);
            throw new IOException(message, e);
        }
    }

    public static List<Structure> loadStructure(String pdbId) {
        StructureManager.PDB_READER.setAutoFetch(true);
        Structure structure;

        try {
            structure = StructureManager.PDB_READER.getStructureById(pdbId);
        } catch (IOException e) {
            StructureManager.LOGGER.error("Failed to fetch PDB id:" + pdbId, e);
            return new ArrayList<Structure>();
        }

        File pdbFile = new File(StructureManager.PDB_DIR, "pdb" + pdbId.toLowerCase() + ".ent.gz");

        if (!pdbFile.exists()) {
            return new ArrayList<Structure>();
        }

        return StructureManager.storeStructureInfo(pdbFile, structure);
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

    private static boolean isMmCif(File file) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);

            if (file.getName().endsWith(".gz")) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(stream), "UTF-8"));
                    String line = reader.readLine();
                    return line != null && line.startsWith("data_");
                } finally {
                    IOUtils.closeQuietly(reader);
                }
            }

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line = reader.readLine();
                return line != null && line.startsWith("data_");
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private static boolean isPdb(File file) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);

            if (file.getName().endsWith(".gz")) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(stream), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("ATOM")) {
                            return true;
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(reader);
                }
            }

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("ATOM")) {
                        return true;
                    }
                }
            } finally {
                IOUtils.closeQuietly(reader);
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return false;
    }

    private static List<Structure> storeStructureInfo(File file, Structure structure) {
        String name = structure.getPDBCode();

        if (name == null || name.trim().equals("")) {
            name = file.getName();
            if (name.endsWith(".pdb") || name.endsWith(".cif")) {
                name = name.substring(0, name.length() - 4);
            } else if (name.endsWith(".pdb.gz") || name.endsWith(".cif.gz")) {
                name = name.substring(0, name.length() - 7);
            }
            structure.setPDBCode(name);
        }

        int count = structure.nrModels();
        int order = 10;
        int leading = 1;
        while (order < count) {
            leading++;
            order *= 10;
        }
        String format = "%s.%0" + leading + "d";

        List<Structure> models = new ArrayList<Structure>();
        for (int i = 0; i < count; i++) {
            Structure clone = structure.clone();
            clone.setChains(structure.getModel(i));
            models.add(clone);

            StructureManager.STRUCTURES.add(new StructureInfo(clone, file, String.format(format, name, i + 1)));
        }
        return models;
    }

    private StructureManager() {
    }
}
