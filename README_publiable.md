# Démineur - projet facultatif

Un démineur jouable, dans une fenêtre JavaFX. Vous n'en écrivez qu'une
classe, `Grille`, qui contient toutes les règles du jeu. Tout le reste est
fourni : la fenêtre, l'application, son lanceur, et onze tests JUnit.

C'est un travail en bonus, à faire seul et à votre rythme : rien à
remettre, rien d'évalué. Un corrigé est publié avec l'énoncé, dans un
document séparé.

## Par où commencer

1. Lisez `Demineur_enonce.pdf`, dans ce dossier.
2. Ouvrez le projet dans IntelliJ comme un projet Maven : JavaFX et JUnit
   sont déclarés dans le `pom.xml`, rien à installer.
3. Écrivez les méthodes de `Grille` qui lèvent `UnsupportedOperationException`,
   dans l'ordre des étapes de l'énoncé.
4. Lancez `GrilleTest` après chaque étape.

## Pour jouer

Clic droit sur **`LanceurDemineur`**, puis `Run`. Pas sur `Demineur` :
lancée directement, cette classe refuse de démarrer. Le commentaire de
`LanceurDemineur` explique pourquoi.

Si ça résiste, passez par la fenêtre Maven : `Plugins`, puis `javafx`, puis
`javafx:run`.

Le projet cible **Java 21**. Vérifiez le SDK dans `File > Project Structure`.
