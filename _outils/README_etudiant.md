# Démineur - projet facultatif

Un démineur jouable, dans une fenêtre JavaFX. Vous n'en écrivez qu'une
classe, `Grille`, qui contient toutes les règles du jeu. Tout le reste est
fourni : la fenêtre, l'application, son lanceur, et onze tests JUnit.

C'est un travail en bonus : rien à remettre, rien d'évalué.

## Par où commencer

1. Lisez `Demineur_enonce.docx`, dans ce dossier.
2. Ouvrez le projet dans IntelliJ comme un projet Maven : JavaFX et JUnit
   sont déclarés dans le `pom.xml`, rien à installer.
3. Écrivez les méthodes de `Grille` qui lèvent `UnsupportedOperationException`,
   dans l'ordre des étapes de l'énoncé.
4. Lancez `GrilleTest` après chaque étape.

## Pour jouer

Clic droit sur **`LanceurDemineur`**, puis `Run`. Pas sur `Demineur` : une
classe qui hérite d'`Application` refuse de démarrer quand Maven fournit
JavaFX. La raison est dans le mémento technique, TEC-FX.4.

Si ça résiste, passez par la fenêtre Maven : `Plugins`, puis `javafx`, puis
`javafx:run`.

Le projet cible **Java 21**. Vérifiez le SDK dans `File > Project Structure`.
