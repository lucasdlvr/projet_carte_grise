package views;

import controllers.PossederController;
import models.Posseder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class PossederView extends JFrame {

    private PossederController controller;
    private JPanel panel;

    public PossederView() {
        controller = new PossederController();  // Initialisation du contrôleur

        // Définir les paramètres de la fenêtre
        setTitle("Gestion des Relations Posséder");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Créer le panneau central pour afficher les relations
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Récupérer toutes les relations Posseder
        List<Posseder> possederList = controller.getAllPosseder();
        for (Posseder posseder : possederList) {
            // Afficher chaque relation sous forme de label
            JPanel possederPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateDebut = sdf.format(posseder.getDateDebutPropriete());
            String dateFin = posseder.getDateFinPropriete() != null ? sdf.format(posseder.getDateFinPropriete()) : "Pas encore définie";

            // Récupérer le matricule du véhicule et le nom/prénom du propriétaire
            String vehiculeMatricule = controller.getVehiculeMatriculeById(posseder.getIdVehicule());
            String proprietaireNomPrenom = controller.getProprietaireNomPrenomById(posseder.getIdProprietaire());

            // Créer un label avec les informations du véhicule, du propriétaire et des dates
            JLabel label = new JLabel("Véhicule: " + vehiculeMatricule + ", " +
                                      "Propriétaire: " + proprietaireNomPrenom + ", " +
                                      "Début: " + dateDebut + ", Fin: " + dateFin);

            // Bouton pour supprimer la relation
            JButton deleteButton = new JButton("Supprimer");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controller.deletePosseder(posseder.getIdProprietaire(), vehiculeMatricule);  // Utiliser matricule véhicule
                    refreshView(); // Rafraîchir la vue après suppression
                }
            });

            // Bouton pour modifier la relation
            JButton modifyButton = new JButton("Modifier");
            modifyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Demander à l'utilisateur d'entrer les nouvelles informations
                    JTextField newDateDebutField = new JTextField(10); // Format: YYYY-MM-DD
                    JTextField newDateFinField = new JTextField(10); // Format: YYYY-MM-DD
                    JTextField newMatriculeField = new JTextField(10); // Matricule du véhicule
                    JTextField newProprietaireNomField = new JTextField(10); // Nom du propriétaire

                    // Pré-remplir les champs avec les valeurs existantes
                    newDateDebutField.setText(sdf.format(posseder.getDateDebutPropriete()));
                    if (posseder.getDateFinPropriete() != null) {
                        newDateFinField.setText(sdf.format(posseder.getDateFinPropriete()));
                    }
                    newMatriculeField.setText(vehiculeMatricule);
                    newProprietaireNomField.setText(proprietaireNomPrenom);

                    Object[] message = {
                            "Nouvelle Date Début Propriété (YYYY-MM-DD):", newDateDebutField,
                            "Nouvelle Date Fin Propriété (YYYY-MM-DD) (optionnel):", newDateFinField,
                            "Nouveau Matricule Véhicule (laisser vide si inchangé):", newMatriculeField,
                            "Nouveau Nom Propriétaire (laisser vide si inchangé):", newProprietaireNomField
                    };

                    int option = JOptionPane.showConfirmDialog(PossederView.this, message, "Modifier une Relation", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        try {
                            // Convertir les nouvelles informations
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date newDateDebut = sdf.parse(newDateDebutField.getText());
                            java.util.Date newDateFin = !newDateFinField.getText().isEmpty() ? sdf.parse(newDateFinField.getText()) : null;

                            // Récupérer les valeurs de matricule et nom, et vérifier s'ils ont changé
                            String newMatricule = newMatriculeField.getText();
                            String newProprietaireNom = newProprietaireNomField.getText();

                            int newIdVehicule = posseder.getIdVehicule();
                            int newIdProprietaire = posseder.getIdProprietaire();

                            // Si le matricule du véhicule a changé, on met à jour l'ID du véhicule
                            if (!newMatricule.isEmpty()) {
                                newIdVehicule = controller.getVehiculeIdByMatricule(newMatricule);
                                if (newIdVehicule == -1) {
                                    JOptionPane.showMessageDialog(PossederView.this, "Véhicule non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }

                            // Si le nom du propriétaire a changé, on met à jour l'ID du propriétaire
                            if (!newProprietaireNom.isEmpty()) {
                                newIdProprietaire = controller.getProprietaireIdByNom(newProprietaireNom);
                                if (newIdProprietaire == -1) {
                                    JOptionPane.showMessageDialog(PossederView.this, "Propriétaire non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }

                            // Appeler le contrôleur pour mettre à jour la relation
                            controller.updatePosseder(newIdProprietaire, newMatricule, newDateDebut, newDateFin);
                            refreshView(); // Rafraîchir la vue après modification
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(PossederView.this, "Erreur dans les données fournies", "Erreur", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            possederPanel.add(label);
            possederPanel.add(modifyButton);
            possederPanel.add(deleteButton);

            panel.add(possederPanel);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        // Ajouter le panneau de boutons pour ajouter et revenir
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton Ajouter
        JButton addButton = new JButton("Ajouter une Relation");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Demander à l'utilisateur d'entrer les informations sans utiliser les IDs
                JTextField matriculeField = new JTextField(10); // Matricule du véhicule
                JTextField proprietaireNomField = new JTextField(10); // Nom du propriétaire
                JTextField dateDebutField = new JTextField(10); // Format: YYYY-MM-DD
                JTextField dateFinField = new JTextField(10); // Format: YYYY-MM-DD

                Object[] message = {
                        "Matricule Véhicule:", matriculeField,
                        "Nom du Propriétaire:", proprietaireNomField,
                        "Date Début Propriété (YYYY-MM-DD):", dateDebutField,
                        "Date Fin Propriété (YYYY-MM-DD) (optionnel):", dateFinField
                };

                int option = JOptionPane.showConfirmDialog(PossederView.this, message, "Ajouter une Relation", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        String matriculeVehicule = matriculeField.getText();
                        String nomProprietaire = proprietaireNomField.getText();
                        int idVehicule = controller.getVehiculeIdByMatricule(matriculeVehicule); // Récupérer l'ID du véhicule
                        int idProprietaire = controller.getProprietaireIdByNom(nomProprietaire); // Récupérer l'ID du propriétaire
                        
                        if (idVehicule == -1 || idProprietaire == -1) {
                            JOptionPane.showMessageDialog(PossederView.this, "Matricule ou propriétaire non trouvé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date dateDebut = sdf.parse(dateDebutField.getText());
                        java.util.Date dateFin = !dateFinField.getText().isEmpty() ? sdf.parse(dateFinField.getText()) : null;
                        
                        // Appeler le contrôleur pour ajouter la relation
                        controller.addPosseder(idProprietaire, matriculeVehicule, dateDebut, dateFin);
                        refreshView();  // Rafraîchir la vue après ajout
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PossederView.this, "Erreur dans les données fournies", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        buttonPanel.add(addButton);

        // Bouton Retour
        JButton backButton = new JButton("Retour");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Fermer la fenêtre
            }
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Rafraîchir la vue après modification ou suppression
    private void refreshView() {
        setVisible(false);
        dispose();
        new PossederView().setVisible(true); // Créer une nouvelle vue pour rafraîchir l'affichage
    }
}
