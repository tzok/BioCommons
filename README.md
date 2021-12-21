![](https://github.com/tzok/BioCommons/workflows/Java%20CI%20with%20Maven/badge.svg)

# Project description

BioCommons is a Java library with classes, data structures and embedded
static knowledge useful in structural bioinformatics.

# Maven

You can use BioCommons by adding the following to your `pom.xml`:

``` xml
<dependency>
  <groupId>pl.poznan.put</groupId>
  <artifactId>BioCommons</artifactId>
  <version>3.0.6</version>
</dependency>
```

# Documentation

You can find the documentation
[here](http://www.cs.put.poznan.pl/tzok/public/static/biocommons/)

# Examples

You can find examples and HOWTOs in the
[wiki](https://github.com/tzok/BioCommons/wiki)

# Functionality

-   Full analysis of PDB and mmCIF files, including missing and modified
    residues, experimental data, etc.
-   An enumeration of atom types, names and aliases used in PDB and
    mmCIF files
-   Atomic bond lengths’ validation
-   Notations from the literature (Zirbel *et al.*, 2009; Leontis and
    Westhof, 2001; Saenger, 1984)
-   Torsion angle analysis for proteins, including varying number of chi
    angle types for different amino acids
-   Torsion angle analysis for nucleic acids, including pseudo-torsion
    angles (Keating *et al.*, 2011) and pseudo-phase pucker of the sugar
    ring (Saenger, 1984)
-   Analysis of circular data (Fisher, 1993), including correct
    averaging of angles
-   Advanced analysis of RNA secondary structure in BPSEQ, CT or
    dot-bracket formats
-   Handling of pseudoknots of any order (Smit *et al.*, 2008)
-   General-purpose constants and utility classes (e.g. handling of SVG
    images)

# Used in

-   [MCQ4Structures](https://github.com/tzok/mcq4structures) (Magnus *et
    al.*, 2020; Wiedemann *et al.*, 2017; Zok *et al.*, 2014)
-   [RNApdbee](http://rnapdbee.cs.put.poznan.pl/) (Zok *et al.*, 2018;
    Antczak *et al.*, 2018; Antczak *et al.*, 2014)
-   [RNAvista](http://rnavista.cs.put.poznan.pl/) (Antczak *et al.*,
    2019; Rybarczyk *et al.*, 2015)
-   [RNAfitme](http://rnafitme.cs.put.poznan.pl/) (Antczak *et al.*,
    2018; Zok *et al.*, 2015)

# Bibliography

<div id="refs" class="references csl-bib-body">

<div id="ref-Magnus2020" class="csl-entry">

RNA-Puzzles toolkit: A computational resource of RNA 3D structure
benchmark datasets, structure manipulation and evaluation tools. M.
Magnus, M. Antczak, T. Zok, J. Wiedemann, P. Lukasiak, Y. Cao, J.M.
Bujnicki, E. Westhof, M. Szachniuk, Z. Miao. *Nucleic Acids Research*.
2020. 48(2):576–588.
doi:[10.1093/nar/gkz1108](https://doi.org/10.1093/nar/gkz1108)

</div>

<div id="ref-Antczak2019" class="csl-entry">

RNAvista: A webserver to assess RNA secondary structures with
non-canonical base pairs. M. Antczak, M. Zablocki, T. Zok, A. Rybarczyk,
J. Blazewicz, M. Szachniuk. *Bioinformatics*. 2019. 35(1):152–155.
doi:[10.1093/bioinformatics/bty609](https://doi.org/10.1093/bioinformatics/bty609)

</div>

<div id="ref-Antczak2018a" class="csl-entry">

RNAfitme: A webserver for modeling nucleobase and nucleoside residue
conformation in fixed-backbone RNA structures. M. Antczak, T. Zok, M.
Osowiecki, M. Popenda, R.W. Adamiak, M. Szachniuk. *BMC Bioinformatics*.
2018. 19(1):304.
doi:[10.1186/s12859-018-2317-9](https://doi.org/10.1186/s12859-018-2317-9)

</div>

<div id="ref-Zok2018" class="csl-entry">

RNApdbee 2.0: Multifunctional tool for RNA structure annotation. T. Zok,
M. Antczak, M. Zurkowski, M. Popenda, J. Blazewicz, R.W. Adamiak, M.
Szachniuk. *Nucleic Acids Research*. 2018. 46(W1):W30–W35.
doi:[10.1093/nar/gky314](https://doi.org/10.1093/nar/gky314)

</div>

<div id="ref-Antczak2018" class="csl-entry">

New algorithms to represent complex pseudoknotted RNA structures in
dot-bracket notation. M. Antczak, M. Popenda, T. Zok, M. Zurkowski, R.W.
Adamiak, M. Szachniuk. *Bioinformatics*. 2018. 34(8):1304–1312.
doi:[10.1093/bioinformatics/btx783](https://doi.org/10.1093/bioinformatics/btx783)

</div>

<div id="ref-Wiedemann2017" class="csl-entry">

LCS-TA to identify similar fragments in RNA 3D structures. J. Wiedemann,
T. Zok, M. Milostan, M. Szachniuk. *BMC Bioinformatics*. 2017.
18(1):456.
doi:[10.1186/s12859-017-1867-6](https://doi.org/10.1186/s12859-017-1867-6)

</div>

<div id="ref-Rybarczyk2015" class="csl-entry">

New in silico approach to assess RNA secondary structures with
non-canonical base pairs. A. Rybarczyk, N. Szostak, M. Antczak, T. Zok,
M. Popenda, R.W. Adamiak, J. Blazewicz, M. Szachniuk. *BMC
Bioinformatics*. 2015. 16(1):276.
doi:[10.1186/s12859-015-0718-6](https://doi.org/10.1186/s12859-015-0718-6)

</div>

<div id="ref-Zok2015" class="csl-entry">

Building the library of RNA 3D nucleotide conformations using clustering
approach. T. Zok, M. Antczak, M. Riedel, D. Nebel, T. Villmann, P.
Lukasiak, J. Blazewicz, M. Szachniuk. *International Journal of Applied
Mathematics and Computer Science*. 2015. 25(3):689–700.
doi:[10.1515/amcs-2015-0050](https://doi.org/10.1515/amcs-2015-0050)

</div>

<div id="ref-Zok2014" class="csl-entry">

MCQ4Structures to compute similarity of molecule structures. T. Zok, M.
Popenda, M. Szachniuk. *Central European Journal of Operations
Research*. 2014. 22(3):457–473.
doi:[10.1007/s10100-013-0296-5](https://doi.org/10.1007/s10100-013-0296-5)

</div>

<div id="ref-Antczak2014" class="csl-entry">

RNApdbee – a webserver to derive secondary structures from pdb files of
knotted and unknotted RNAs. M. Antczak, T. Zok, M. Popenda, P. Lukasiak,
R.W. Adamiak, J. Blazewicz, M. Szachniuk. *Nucleic Acids Research*.
2014. 42(W1):W368–W372.
doi:[10.1093/nar/gku330](https://doi.org/10.1093/nar/gku330)

</div>

<div id="ref-Keating2011" class="csl-entry">

A new way to see RNA. K.S. Keating, E.L. Humphris, A.M. Pyle. *Quarterly
Reviews of Biophysics*. 2011. 44(4):433–466.
doi:[10.1017/S0033583511000059](https://doi.org/10.1017/S0033583511000059)

</div>

<div id="ref-Zirbel2009" class="csl-entry">

Classification and energetics of the base-phosphate interactions in RNA.
C.L. Zirbel, J.E. Šponer, J. Šponer, J. Stombaugh, N.B. Leontis.
*Nucleic Acids Research*. 2009. 37(15):4898–4918.
doi:[10.1093/nar/gkp468](https://doi.org/10.1093/nar/gkp468)

</div>

<div id="ref-Smit2008" class="csl-entry">

From knotted to nested RNA structures: A variety of computational
methods for pseudoknot removal. S. Smit, K. Rother, J. Heringa, R.
Knight. *RNA*. 2008. 14(3):410–416.
doi:[10.1261/rna.881308](https://doi.org/10.1261/rna.881308)

</div>

<div id="ref-Leontis2001" class="csl-entry">

Geometric nomenclature and classification of RNA base pairs. N.B.
Leontis, E. Westhof. *RNA*. 2001. 7(4):499–512.
doi:[10.1017/S1355838201002515](https://doi.org/10.1017/S1355838201002515)

</div>

<div id="ref-Fisher1993" class="csl-entry">

Statistical Analysis of Circular Data. N.I. Fisher.

</div>

<div id="ref-Saenger1984" class="csl-entry">

Principles of Nucleic Acid Structure. W. Saenger.

</div>

</div>
