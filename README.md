# Démineur

Le projet de travail du professeur, organisé comme GameHub : `src/` contient
le **corrigé**, et le projet que reçoit l'étudiant en est dérivé.

## Ce qu'on y trouve

| Dossier | Contenu |
|---|---|
| `src/` | le corrigé ; les méthodes que l'étudiant écrit sont précédées d'un marqueur `//@ecrire D.n` |
| `_outils/` | les générateurs de l'énoncé et du corrigé, celui du projet étudiant, et leurs utilitaires |
| `_documents/` | `Demineur_enonce.docx`, l'énoncé en sept étapes ; `Demineur_corrige.docx`, le corrigé publié à part |
| `_documents/schemas/` | les illustrations : `schemas.py` dessine les schémas en SVG et les rend en PNG (cairosvg) ; `capture_partie.png` est une capture d'écran du jeu, prise à la main |
| `depot/` | le projet étudiant généré, et son zip ; ignoré par Git |

## Pour régénérer

`sh _outils/build_all.sh` enchaîne tout (schémas, deux documents, pagination,
typographie, PDF si LibreOffice est installé, projet étudiant). Étape par étape :

```
python3 _documents/schemas/schemas.py           les schémas (SVG + PNG)
node _outils/build_demineur.js                  l'énoncé
node _outils/build_corrige.js                   le corrigé
python3 _outils/ajouter_pagination.py _documents/Demineur_enonce.docx "Démineur" <pages>
python3 _outils/ajouter_pagination.py _documents/Demineur_corrige.docx "Démineur - corrigé" <pages>
python3 _outils/typographie.py _documents/*.docx
python3 _outils/depot_demineur.py --verifier    contrôle le squelette
python3 _outils/depot_demineur.py               fabrique depot/Demineur/ et depot/Demineur.zip
```

Sur Moodle, l'énoncé et le corrigé sont déposés en PDF (export depuis Word),
à côté de `Demineur.zip`. Le zip contient l'énoncé en PDF (pas le `.docx`,
ni le corrigé) : exporter le PDF avant de lancer `depot_demineur.py`.

Le corrigé recopie le code des cinq méthodes de `Grille` : si le corrigé
de `src/` change, `build_corrige.js` doit suivre, comme l'énoncé.

Le projet étudiant reçoit `Grille` avec le corps des méthodes marquées
remplacé par une exception qui nomme l'étape, et tout le reste tel quel : la
vue, l'application, le lanceur, les onze tests et l'énoncé. Les étapes de
l'énoncé et les marqueurs du code doivent rester d'accord : si l'un change,
l'autre suit.

## L'application

Une application JavaFX autonome, en quatre classes.

| Classe | Rôle |
|---|---|
| `Grille` | le **modèle** : les mines, les cases découvertes, les drapeaux, la victoire. Aucun import JavaFX. |
| `VueDemineur` | la **vue** et le **contrôleur** : elle traduit un clic en appel au modèle, puis redessine. |
| `Demineur` | l'application : hérite d'`Application`, reçoit une fenêtre, la remplit. |
| `LanceurDemineur` | le point d'entrée. Il n'hérite de rien, et c'est tout son intérêt. |

## Pour le lancer

Clic droit sur **`LanceurDemineur`** → `Run`. Pas sur `Demineur`.

### Pourquoi pas sur `Demineur`

Parce qu'il refuserait de démarrer :

```
Erreur : des composants d'exécution JavaFX obligatoires
pour exécuter cette application sont manquants.
```

Le lanceur du JDK examine la classe qu'on lui demande d'exécuter. Si elle hérite
d'`Application`, il exige que JavaFX soit chargé comme **module** — avec
`--module-path` et `--add-modules` — et il s'arrête là si ce n'est pas le cas.
Maven, lui, place les bibliothèques sur le classpath ordinaire. Les deux ne
s'entendent pas.

`LanceurDemineur` n'hérite de rien. Le contrôle ne se déclenche pas, et
`Application.launch` démarre JavaFX depuis le classpath sans protester. C'est la
forme recommandée dès qu'on distribue une application JavaFX sans passer par les
modules.

### La version Java

Le projet cible **Java 21**. Vérifiez le SDK dans `File > Project Structure` :
un JDK plus récent produit des messages déroutants, dont un qui prétend que la
classe ne contient pas de `main`.

### Si ça résiste

Passez par la fenêtre Maven : `Plugins` → `javafx` → `javafx:run`. Le plugin
monte le chemin de modules lui-même, et il ignore la configuration d'exécution
d'IntelliJ.

## Pourquoi ce projet existe séparément

Le démineur a d'abord été écrit comme un jeu du fil rouge GameHub, à côté du
Nim et de la bataille. Ça ne tenait pas.

Le hub est une application **console**. Ouvrir une fenêtre depuis une console
oblige à démarrer le moteur graphique à la main, à construire l'interface sur
un autre fil d'exécution, et à faire attendre le premier. Trois notions qui ne
sont ni au programme ni à l'examen, pour un seul jeu — et une erreur au
premier lancement :

```
Caused by: java.lang.IllegalStateException: Toolkit not initialized
```

Sorti du hub, le démineur a retrouvé la forme ordinaire d'une application
JavaFX : `launch()` démarre le moteur, `start()` reçoit une fenêtre prête.
C'est celle des exercices `FX`, et celle de la question 5 de l'examen.

**La leçon vaut d'être dite en classe :** quand le code devient compliqué pour
tenir dans une structure, c'est souvent la structure qu'il faut changer.

## Les marques : trois états, deux significations

Le clic de marquage fait tourner la case.

| Affiché | État | Ce que ça veut dire | Le clic gauche |
|---|---|---|---|
| *(vide)* | `CACHEE` | rien de décidé | découvre |
| `*` | `DRAPEAU` | il y a une mine ici | **bloqué** |
| `?` | `DOUTE` | peut-être | découvre |

La distinction vient du jeu d'origine et elle a du sens : un drapeau est une
décision, un point d'interrogation est une note qu'on se laisse. Seule la
décision protège la case.

C'est `Grille` qui décide de l'enchaînement, pas la vue. Celle-ci demande la
marque suivante et affiche ce qu'on lui rend — la séparation modèle/vue, à
l'échelle d'un détail.

## Poser une marque : trois gestes

Le clic droit est la manière naturelle, mais il n'est pas toujours disponible :
sur le pavé tactile d'un portable Apple, il demande un réglage ou un appui à
deux doigts que tout le monde n'a pas activé.

`VueDemineur` accepte donc aussi **Maj+clic** et **Ctrl+clic**. Le premier ne se
heurte à rien ; le second est parfois traduit par le système en clic droit,
auquel cas c'est la première condition qui répond — les deux chemins mènent au
même endroit.

C'est une leçon qui dépasse ce jeu, et qui vaut d'être dite en classe : une
interface qui n'offre qu'une seule façon de faire quelque chose exclut ceux dont
le matériel ne la permet pas. En ajouter une seconde coûte trois lignes.

## Publié avant le module FX

Le démineur est donné en auto-apprentissage, et il arrive avant le module
FX. Ni l'énoncé, ni le corrigé, ni les commentaires du code ne s'appuient
donc sur ce module : pas de renvoi aux exercices FX, au deck FX2 ou à la
calculatrice. L'étudiant n'écrit que `Grille`, qui n'a rien de JavaFX ; le
lancement est expliqué sommairement dans l'énoncé (D.7) et plus longuement
dans `LanceurDemineur`. Seul le renvoi au mémento technique (TEC-FX.4)
subsiste, comme lecture complémentaire.

## Un reste à surveiller

Le démineur a vécu dans le fil rouge, et son texte s'en souvenait : il invitait
encore à fermer la fenêtre « pour revenir au hub ». Corrigé le 14/09.

Si vous en trouvez d'autres, cherchez `hub` dans les chaînes de caractères —
les commentaires, eux, mentionnent le hub à bon droit puisqu'ils racontent
pourquoi ce projet existe séparément.

## Ce qui reste testable

`Grille` ne contient aucun import JavaFX, et `GrilleTest` en vérifie toutes
les règles du jeu : le comptage des mines voisines, la propagation des cases
vides, les drapeaux, la victoire, et le refus d'une grille impossible. C'est
le bénéfice de la séparation modèle / vue, et il se vérifie en lançant les
tests. Seul le placement des mines au hasard échappe aux tests : le `Random`
est créé dans `Grille`, et son résultat ne se prédit pas.
