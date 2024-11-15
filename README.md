![](https://github.com/tzok/BioCommons/workflows/Java%20CI%20with%20Maven/badge.svg)

# Project description

BioCommons is a Java library with classes, data structures and embedded
static knowledge useful in structural bioinformatics.

If you use BioCommons in your research, please cite:

> BioCommons: A Robust Java Library for RNA Structural Bioinformatics.
> T. Zok. *Bioinformatics*. 2021. 37(17):2766–2767.
> doi:[10.1093/bioinformatics/btab069](https://doi.org/10.1093/bioinformatics/btab069)

# Maven

You can use BioCommons by adding the following to your `pom.xml`:

``` xml
<dependency>
  <groupId>pl.poznan.put</groupId>
  <artifactId>BioCommons</artifactId>
  <version>3.1.11</version>
</dependency>
```

# Documentation

You can find the documentation
[here](http://www.cs.put.poznan.pl/tzok/public/static/biocommons/)

# Examples

You can find examples and HOWTOs in the
[wiki](https://github.com/tzok/BioCommons/wiki)

# Functionality

- Full analysis of PDB and mmCIF files, including missing and modified
  residues, experimental data, etc.
- An enumeration of atom types, names and aliases used in PDB and mmCIF
  files
- Atomic bond lengths’ validation
- Notations from the literature (Zirbel *et al.* 2009, Leontis *et al.*
  2001, Saenger 1984)
- Torsion angle analysis for proteins, including varying number of chi
  angle types for different amino acids
- Torsion angle analysis for nucleic acids, including pseudo-torsion
  angles (Keating *et al.* 2011) and pseudo-phase pucker of the sugar
  ring (Saenger 1984)
- Analysis of circular data (Fisher 1993), including correct averaging
  of angles
- Advanced analysis of RNA secondary structure in BPSEQ, CT or
  dot-bracket formats
- Handling of pseudoknots of any order (Smit *et al.* 2008)
- General-purpose constants and utility classes (e.g. handling of SVG
  images)

# Used in

- [MCQ4Structures](https://github.com/tzok/mcq4structures) (Magnus *et
  al.* 2020, Wiedemann *et al.* 2017, Zok *et al.* 2014)
- [RNApdbee](http://rnapdbee.cs.put.poznan.pl/) (Zok *et al.* 2018,
  Antczak *et al.* 2018, Antczak *et al.* 2014)
- [RNAvista](http://rnavista.cs.put.poznan.pl/) (Antczak *et al.* 2019,
  Rybarczyk *et al.* 2015)
- [RNAfitme](http://rnafitme.cs.put.poznan.pl/) (Antczak *et al.* 2018,
  Zok *et al.* 2015)

# Bibliography

<div id="refs" class="references csl-bib-body">

1.  BioCommons: A Robust Java Library for RNA Structural
    Bioinformatics. T. Zok. *Bioinformatics*. 2021. 37(17):2766–2767.
    doi:[10.1093/bioinformatics/btab069](https://doi.org/10.1093/bioinformatics/btab069)

2.  RNA-Puzzles Toolkit: A Computational Resource of RNA 3D Structure
    Benchmark Datasets, Structure Manipulation and Evaluation Tools. M.
    Magnus, M. Antczak, T. Zok, J. Wiedemann, P. Lukasiak, Y. Cao, J.M.
    Bujnicki, E. Westhof, M. Szachniuk, Z. Miao. *Nucleic Acids
    Research*. 2020. 48(2):576–588.
    doi:[10.1093/nar/gkz1108](https://doi.org/10.1093/nar/gkz1108)

3.  RNAvista: A Webserver to Assess RNA Secondary Structures with
    Non-Canonical Base Pairs. M. Antczak, M. Zablocki, T. Zok, A.
    Rybarczyk, J. Blazewicz, M. Szachniuk. *Bioinformatics*. 2019.
    35(1):152–155.
    doi:[10.1093/bioinformatics/bty609](https://doi.org/10.1093/bioinformatics/bty609)

4.  RNAfitme: A Webserver for Modeling Nucleobase and Nucleoside Residue
    Conformation in Fixed-Backbone RNA Structures. M. Antczak, T.
    Zok, M. Osowiecki, M. Popenda, R.W. Adamiak, M. Szachniuk. *BMC
    Bioinformatics*. 2018. 19(1):304.
    doi:[10.1186/s12859-018-2317-9](https://doi.org/10.1186/s12859-018-2317-9)

5.  RNApdbee 2.0: Multifunctional Tool for RNA Structure Annotation. T.
    Zok, M. Antczak, M. Zurkowski, M. Popenda, J. Blazewicz, R.W.
    Adamiak, M. Szachniuk. *Nucleic Acids Research*. 2018.
    46(W1):W30–W35.
    doi:[10.1093/nar/gky314](https://doi.org/10.1093/nar/gky314)

6.  New Algorithms to Represent Complex Pseudoknotted RNA Structures in
    Dot-Bracket Notation. M. Antczak, M. Popenda, T. Zok, M. Zurkowski,
    R.W. Adamiak, M. Szachniuk. *Bioinformatics*. 2018. 34(8):1304–1312.
    doi:[10.1093/bioinformatics/btx783](https://doi.org/10.1093/bioinformatics/btx783)

7.  LCS-TA to Identify Similar Fragments in RNA 3D Structures. J.
    Wiedemann, T. Zok, M. Milostan, M. Szachniuk. *BMC
    Bioinformatics*. 2017. 18(1):456.
    doi:[10.1186/s12859-017-1867-6](https://doi.org/10.1186/s12859-017-1867-6)

8.  New in Silico Approach to Assess RNA Secondary Structures with
    Non-Canonical Base Pairs. A. Rybarczyk, N. Szostak, M. Antczak, T.
    Zok, M. Popenda, R.W. Adamiak, J. Blazewicz, M. Szachniuk. *BMC
    Bioinformatics*. 2015. 16(1):276.
    doi:[10.1186/s12859-015-0718-6](https://doi.org/10.1186/s12859-015-0718-6)

9.  Building the Library of RNA 3D Nucleotide Conformations Using
    Clustering Approach. T. Zok, M. Antczak, M. Riedel, D. Nebel, T.
    Villmann, P. Lukasiak, J. Blazewicz, M. Szachniuk. *International
    Journal of Applied Mathematics and Computer Science*. 2015.
    25(3):689–700.
    doi:[10.1515/amcs-2015-0050](https://doi.org/10.1515/amcs-2015-0050)

10. MCQ4Structures to Compute Similarity of Molecule Structures. T.
    Zok, M. Popenda, M. Szachniuk. *Central European Journal of
    Operations Research*. 2014. 22(3):457–473.
    doi:[10.1007/s10100-013-0296-5](https://doi.org/10.1007/s10100-013-0296-5)

11. RNApdbee – a Webserver to Derive Secondary Structures from Pdb Files
    of Knotted and Unknotted RNAs. M. Antczak, T. Zok, M. Popenda, P.
    Lukasiak, R.W. Adamiak, J. Blazewicz, M. Szachniuk. *Nucleic Acids
    Research*. 2014. 42(W1):W368–W372.
    doi:[10.1093/nar/gku330](https://doi.org/10.1093/nar/gku330)

12. A New Way to See RNA. K.S. Keating, E.L. Humphris, A.M. Pyle.
    *Quarterly Reviews of Biophysics*. 2011. 44(4):433–466.
    doi:[10.1017/S0033583511000059](https://doi.org/10.1017/S0033583511000059)

13. Classification and Energetics of the Base-Phosphate Interactions in
    RNA. C.L. Zirbel, J.E. Šponer, J. Šponer, J. Stombaugh, N.B.
    Leontis. *Nucleic Acids Research*. 2009. 37(15):4898–4918.
    doi:[10.1093/nar/gkp468](https://doi.org/10.1093/nar/gkp468)

14. From Knotted to Nested RNA Structures: A Variety of Computational
    Methods for Pseudoknot Removal. S. Smit, K. Rother, J. Heringa, R.
    Knight. *RNA*. 2008. 14(3):410–416.
    doi:[10.1261/rna.881308](https://doi.org/10.1261/rna.881308)

15. Geometric Nomenclature and Classification of RNA Base Pairs. N.B.
    Leontis, E. Westhof. *RNA*. 2001. 7(4):499–512.
    doi:[10.1017/S1355838201002515](https://doi.org/10.1017/S1355838201002515)

16. [Statistical Analysis of Circular
    Data](https://doi.org/10.1017/CBO9780511564345). N.I. Fisher.

17. [Principles of Nucleic Acid
    Structure](https://doi.org/10.1007/978-1-4612-5190-3). W. Saenger.

</div>
