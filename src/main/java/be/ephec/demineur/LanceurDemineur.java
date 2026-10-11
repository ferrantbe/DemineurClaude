package be.ephec.demineur;

import javafx.application.Application;

/**
 * LE POINT D'ENTREE. C'est cette classe qu'il faut lancer, pas Demineur.
 *
 * POURQUOI ELLE EXISTE. Elle ne fait rien d'autre qu'appeler Demineur, et
 * pourtant sans elle, le programme refuse de demarrer :
 *
 *     Erreur : des composants d'execution JavaFX obligatoires pour
 *     executer cette application sont manquants.
 *
 * Le lanceur du JDK examine la classe qu'on lui demande d'executer. Si
 * elle herite d'Application, il exige que JavaFX soit charge comme
 * MODULE — avec --module-path et --add-modules — et il s'arrete la si ce
 * n'est pas le cas. Maven, lui, place les bibliotheques sur le classpath
 * ordinaire. Les deux ne s'entendent pas.
 *
 * Cette classe n'herite de rien. Le controle ne se declenche donc pas, et
 * Application.launch demarre JavaFX depuis le classpath sans protester.
 *
 * CE N'EST PAS UN BRICOLAGE : c'est la forme recommandee des que l'on
 * distribue une application JavaFX sans passer par les modules, et vous
 * la retrouverez telle quelle dans beaucoup de projets.
 *
 * DEUX AUTRES SOLUTIONS EXISTENT, et elles ont leur prix.
 *
 *   - Ajouter --module-path et --add-modules aux options de la machine
 *     virtuelle, dans la configuration d'execution. Ca marche, mais le
 *     chemin depend de la machine : la configuration n'est plus
 *     transportable d'un poste a l'autre.
 *
 *   - Declarer un module-info.java. C'est la solution propre, celle qu'on
 *     emploie dans les projets JavaFX du cours, mais elle impose que
 *     toutes les dependances soient modulaires et elle complique le
 *     demarrage. Hors sujet pour un projet de trois classes.
 */
public class LanceurDemineur {

    public static void main(String[] args) {
        Application.launch(Demineur.class, args);
    }
}
