#!/usr/bin/env python3
"""
depot_demineur.py — fabrique le projet que recoit l'etudiant.

    python3 _outils/depot_demineur.py            le dossier depot/ et son zip
    python3 _outils/depot_demineur.py --verifier seulement les controles

La source unique
----------------
Comme pour GameHub, le code n'est ecrit qu'une fois : src/ contient le
CORRIGE. Le projet de l'etudiant en est derive.

Dans Grille.java, chaque methode que l'etudiant ecrit est precedee d'un
marqueur, ignore par le compilateur :

    //@ecrire D.2
    public int minesVoisines(int ligne, int colonne) {
        ...
    }

Le script garde la signature et la Javadoc, et remplace le corps par une
exception qui dit quelle etape l'attend. Tout le reste est copie tel quel :
la vue, l'application, le lanceur, les tests, et l'enonce.

Ce que --verifier controle
--------------------------
Que chaque marqueur precede bien une methode, que le squelette garde les
memes signatures publiques que le corrige, et que les accolades restent
equilibrees. Il ne compile pas : le squelette ne compile qu'avec JavaFX
et JUnit, que seul Maven fournit.
"""

import re
import shutil
import sys
import zipfile
from pathlib import Path

RACINE = Path(__file__).resolve().parent.parent
PAQUET = Path("be/ephec/demineur")
DEPOT = RACINE / "depot" / "Demineur"
MARQUEUR = re.compile(r"^(\s*)//@ecrire\s+(.+?)\s*$")
SIGNATURE = re.compile(r"^\s*public\s+[^=;]*\(.*\)\s*\{\s*$")

EN_TETE_CORRIGE = re.compile(
    r" \* Le MODELE du demineur\. CORRIGE\.\n \*\n(?: \*[^\n]*\n)*? \*\n")
EN_TETE_SQUELETTE = (
    " * Le MODELE du demineur. A COMPLETER.\n *\n"
    " * Les methodes qui levent UnsupportedOperationException sont les\n"
    " * votres : l'enonce dit dans quel ordre les ecrire, et quels tests\n"
    " * passent au vert a chaque etape. Tout le reste est fourni.\n *\n")


def fin_du_bloc(lignes, depart):
    """Indice de la ligne qui ferme le bloc ouvert a `depart`."""
    profondeur = 0
    for i in range(depart, len(lignes)):
        profondeur += lignes[i].count("{") - lignes[i].count("}")
        if profondeur == 0:
            return i
    raise ValueError(f"accolade fermante introuvable apres la ligne {depart + 1}")


def squelette_de(texte):
    """Rend le texte de Grille tel que l'etudiant le recoit."""
    lignes = texte.split("\n")
    sortie = []
    i = 0
    while i < len(lignes):
        trouve = MARQUEUR.match(lignes[i])
        if not trouve:
            sortie.append(lignes[i])
            i += 1
            continue
        indentation, etape = trouve.group(1), trouve.group(2)
        signature = lignes[i + 1]
        if not SIGNATURE.match(signature):
            raise ValueError(f"le marqueur de la ligne {i + 1} ne precede pas une methode")
        fin = fin_du_bloc(lignes, i + 1)
        sortie.append(signature)
        sortie.append(f'{indentation}    throw new UnsupportedOperationException("{etape} : a ecrire");')
        sortie.append(lignes[fin])
        i = fin + 1

    texte = "\n".join(sortie)
    texte, remplacements = EN_TETE_CORRIGE.subn(EN_TETE_SQUELETTE, texte, count=1)
    if remplacements != 1:
        raise ValueError("en-tete du corrige introuvable : la Javadoc de Grille a change")
    return texte


def signatures(texte):
    return sorted(l.strip() for l in texte.split("\n") if SIGNATURE.match(l))


def verifier():
    corrige = (RACINE / "src/main/java" / PAQUET / "Grille.java").read_text(encoding="utf-8")
    squelette = squelette_de(corrige)
    a_ecrire = sum(1 for l in corrige.split("\n") if MARQUEUR.match(l))
    problemes = []
    if signatures(corrige) != signatures(squelette):
        problemes.append("les signatures publiques different entre corrige et squelette")
    if squelette.count("{") != squelette.count("}"):
        problemes.append("le squelette n'a pas autant d'accolades ouvrantes que fermantes")
    if "@ecrire" in squelette or "CORRIGE" in squelette:
        problemes.append("une trace du corrige est restee dans le squelette")
    if problemes:
        print("\n".join("   " + p for p in problemes))
        sys.exit(1)
    print(f"Squelette coherent : {a_ecrire} methodes a ecrire, "
          f"{len(signatures(corrige))} signatures publiques identiques.")
    return squelette


def fabriquer():
    squelette = verifier()
    if DEPOT.parent.exists():
        shutil.rmtree(DEPOT.parent)
    DEPOT.mkdir(parents=True)

    for nom in ["pom.xml", ".gitignore"]:
        shutil.copy(RACINE / nom, DEPOT / nom)
    shutil.copy(RACINE / "README_publiable.md", DEPOT / "README.md")

    principal = DEPOT / "src/main/java" / PAQUET
    principal.mkdir(parents=True)
    for source in (RACINE / "src/main/java" / PAQUET).glob("*.java"):
        if source.name == "Grille.java":
            (principal / source.name).write_text(squelette, encoding="utf-8")
        else:
            shutil.copy(source, principal / source.name)

    tests = DEPOT / "src/test/java" / PAQUET
    tests.mkdir(parents=True)
    for source in (RACINE / "src/test/java" / PAQUET).glob("*.java"):
        shutil.copy(source, tests / source.name)

    enonce = RACINE / "_documents" / "Demineur_enonce.docx"
    if enonce.exists():
        shutil.copy(enonce, DEPOT / enonce.name)
    else:
        print("   attention : _documents/Demineur_enonce.docx absent, "
              "lancez d'abord node _outils/build_demineur.js")

    archive = DEPOT.parent / "Demineur.zip"
    with zipfile.ZipFile(archive, "w", zipfile.ZIP_DEFLATED) as z:
        for fichier in sorted(DEPOT.rglob("*")):
            if fichier.is_file():
                z.write(fichier, fichier.relative_to(DEPOT.parent))
    print(f"Projet etudiant : {DEPOT.relative_to(RACINE)}/ et {archive.relative_to(RACINE)}")


if __name__ == "__main__":
    if "--verifier" in sys.argv:
        verifier()
    else:
        fabriquer()
