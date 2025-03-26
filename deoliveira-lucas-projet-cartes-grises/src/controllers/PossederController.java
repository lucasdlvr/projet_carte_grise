package controllers;

import database.DatabaseConnection;
import models.Posseder;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PossederController {

    // Récupérer toutes les relations Posseder
    public List<Posseder> getAllPosseder() {
        List<Posseder> possederList = new ArrayList<>();
        String query = "SELECT * FROM POSSEDER"; // Requête pour récupérer toutes les relations
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                possederList.add(new Posseder(
                        rs.getInt("id_proprietaire"),
                        rs.getInt("id_vehicule"),
                        rs.getDate("date_debut_propriete"),
                        rs.getDate("date_fin_propriete")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return possederList;
    }

    // Modifier une relation Posseder (propriétaire, véhicule, dates)
    public void updatePosseder(int idProprietaire, String matriculeVehicule, java.util.Date newDateDebut, java.util.Date newDateFin) {
        int idVehicule = getVehiculeIdByMatricule(matriculeVehicule); // Récupérer l'ID du véhicule à partir du matricule
        if (idVehicule == -1) {
            return;  // Véhicule non trouvé
        }

        String query = "UPDATE POSSEDER SET id_vehicule = ?, id_proprietaire = ?, date_debut_propriete = ?, date_fin_propriete = ? " +
                "WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idVehicule);
            ps.setInt(2, idProprietaire);
            ps.setDate(3, new java.sql.Date(newDateDebut.getTime()));
            ps.setDate(4, newDateFin != null ? new java.sql.Date(newDateFin.getTime()) : null);
            ps.setInt(5, idProprietaire);
            ps.setInt(6, idVehicule);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer le matricule d'un véhicule par son ID
    public String getVehiculeMatriculeById(int idVehicule) {
        String query = "SELECT matricule FROM VEHICULE WHERE id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idVehicule);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("matricule");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer le modèle d'un véhicule par son ID
    public String getModeleByVehiculeId(int idVehicule) {
        String query = "SELECT modele FROM VEHICULE WHERE id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idVehicule);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("modele");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer le nom et prénom du propriétaire par son ID
    public String getProprietaireNomPrenomById(int idProprietaire) {
        String query = "SELECT nom, prenom FROM PROPRIETAIRE WHERE id_proprietaire = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietaire);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom") + " " + rs.getString("prenom");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ajouter une nouvelle relation Posseder
    public void addPosseder(int idProprietaire, String matriculeVehicule, java.util.Date dateDebut, java.util.Date dateFin) {
        int idVehicule = getVehiculeIdByMatricule(matriculeVehicule); // Récupérer l'ID du véhicule à partir du matricule
        if (idVehicule == -1) {
            return;  // Véhicule non trouvé
        }

        // Vérifier si la relation existe déjà
        if (isRelationExists(idProprietaire, idVehicule)) {
            // Afficher un message d'erreur si la relation existe déjà
            JOptionPane.showMessageDialog(null, "La relation entre ce propriétaire et ce véhicule existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO POSSEDER (id_proprietaire, id_vehicule, date_debut_propriete, date_fin_propriete) " +
                       "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);
            ps.setDate(3, new java.sql.Date(dateDebut.getTime()));
            ps.setDate(4, dateFin != null ? new java.sql.Date(dateFin.getTime()) : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprimer une relation Posseder
    public void deletePosseder(int idProprietaire, String matriculeVehicule) {
        int idVehicule = getVehiculeIdByMatricule(matriculeVehicule); // Récupérer l'ID du véhicule à partir du matricule
        if (idVehicule == -1) {
            return;  // Véhicule non trouvé
        }

        String query = "DELETE FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer l'ID du véhicule par matricule
    public int getVehiculeIdByMatricule(String matricule) {
        String query = "SELECT id_vehicule FROM VEHICULE WHERE matricule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, matricule);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vehicule");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Retourner -1 si aucun véhicule n'est trouvé
    }

    // Récupérer l'ID du propriétaire par nom
    public int getProprietaireIdByNom(String nomPrenom) {
        String query = "SELECT id_proprietaire FROM PROPRIETAIRE " +
                       "WHERE CONCAT(nom, ' ', prenom) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nomPrenom);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_proprietaire");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Retourner -1 si le propriétaire n'est pas trouvé
    }

    // Vérifier si une relation entre le propriétaire et le véhicule existe déjà
    public boolean isRelationExists(int idProprietaire, int idVehicule) {
        String query = "SELECT COUNT(*) FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idProprietaire);
            ps.setInt(2, idVehicule);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Si le count est supérieur à 0, la relation existe déjà
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // La relation n'existe pas
    }
}
