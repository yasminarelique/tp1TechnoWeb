package simplejdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DAO {

	private final DataSource myDataSource;

	/**
	 *
	 * @param dataSource la source de données à utiliser
	 */
	public DAO(DataSource dataSource) {
		this.myDataSource = dataSource;
	}

	/**
	 *
	 * @return le nombre d'enregistrements dans la table CUSTOMER
	 * @throws DAOException
	 */
	public int numberOfCustomers() throws DAOException {
		int result = 0;

		String sql = "SELECT COUNT(*) AS NUMBER FROM CUSTOMER";
		// Syntaxe "try with resources" 
		// cf. https://stackoverflow.com/questions/22671697/try-try-with-resources-and-connection-statement-and-resultset-closing
		try (Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			Statement stmt = connection.createStatement(); // On crée un statement pour exécuter une requête
			ResultSet rs = stmt.executeQuery(sql) // Un ResultSet pour parcourir les enregistrements du résultat
			) {
			if (rs.next()) { // Pas la peine de faire while, il y a 1 seul enregistrement
				// On récupère le champ NUMBER de l'enregistrement courant
				result = rs.getInt("NUMBER");
			}
		} catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}

		return result;
	}
	
	/**
	 * Detruire un enregistrement dans la table CUSTOMER
	 * @param customerId la clé du client à détruire
	 * @return le nombre d'enregistrements détruits (1 ou 0 si pas trouvé)
	 * @throws DAOException
	 */
	public int deleteCustomer(int customerId) throws DAOException {

		// Une requête SQL paramétrée
		String sql = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?"; 
		try (   Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			PreparedStatement stmt = connection.prepareStatement(sql) // On crée un statement préparé pour exécuter une requête paramétrée
                ) {
                    
                    
                        // Définir la valeur du paramètre
			stmt.setInt(1, customerId);
			// retourner le nombre de changement effectué dans la base de donnée
			return stmt.executeUpdate();

		}  catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
	}	

	/**
	 *
	 * @param customerId la clé du client à recherche
	 * @return le nombre de bons de commande pour ce client (table PURCHASE_ORDER)
	 * @throws DAOException
	 */
	public int numberOfOrdersForCustomer(int customerId) throws DAOException {
		int result = 0;

		// Une requête SQL paramétrée
		String sql = "SELECT COUNT(*) AS NUMBER FROM PURCHASE_ORDER WHERE CUSTOMER_ID = ?";
		try (   Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			PreparedStatement stmt = connection.prepareStatement(sql) // On crée un statement préparé pour exécuter une requête paramétrée       
                ) {
                  
                        // Définir la valeur du paramètre
			stmt.setInt(1, customerId);
                        //On teste l'execution
			try(ResultSet rs = stmt.executeQuery()){
                        rs.next(); //On a un seul enregistrement 
                        result = rs.getInt("NUMBER"); //On recupere le resultat de la requete
			
                        }
		}  catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
                
                return result;
	}

	/**
	 * Trouver un Customer à partir de sa clé
	 *
	 * @param customerID la clé du CUSTOMER à rechercher
	 * @return l'enregistrement correspondant dans la table CUSTOMER, ou null si pas trouvé
	 * @throws DAOException
	 */
	CustomerEntity findCustomer(int customerID) throws DAOException {
            CustomerEntity result;
		// Une requête SQL paramétrée
		String sql = "SELECT CUSTOMER_ID, name, addressline1 FROM CUSTOMER WHERE CUSTOMER_ID = ?";
		try (   Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			PreparedStatement stmt = connection.prepareStatement(sql)  // On crée un statement préparé pour exécuter une requête paramétrée          
                ) {
                  
                        // Définir la valeur du paramètre
			stmt.setInt(1, customerID);
                        
			try(ResultSet rs = stmt.executeQuery()){
                        rs.next(); //On a un seul enregistrement 
                        result = new CustomerEntity(rs.getInt("CUSTOMER_ID"),rs.getString("name"),rs.getString("addressline1"));
                      
			
                        }
		}  catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
                
                 return result;
	}

	/**
	 * Liste des clients localisés dans un état des USA
	 *
	 * @param state l'état à rechercher (2 caractères)
	 * @return la liste des clients habitant dans cet état
	 * @throws DAOException
	 */
	List<CustomerEntity> customersInState(String state) throws DAOException {
		ArrayList<CustomerEntity> result = new ArrayList();
                CustomerEntity resultligne;
		// Une requête SQL paramétrée (selection des clients avec un state correspondant au paramètre entré)
		String sql = "SELECT CUSTOMER_ID, name, addressline1 FROM CUSTOMER WHERE STATE = ?";
		try (   Connection connection = myDataSource.getConnection(); // Ouvrir une connexion
			PreparedStatement stmt = connection.prepareStatement(sql)    // On crée un statement préparé pour exécuter une requête paramétrée        
                ) {
                  
                        // Définir la valeur du paramètre
			stmt.setString(1, state);
                        
			try(ResultSet rs = stmt.executeQuery()){
                            while (rs.next()) { // On test tous les enregistrements
                                resultligne = new CustomerEntity(rs.getInt("CUSTOMER_ID"),rs.getString("name"),rs.getString("addressline1"));
                                //Completer la liste de client
                                result.add(resultligne);
			}
                        //rs.next(); //On a un seul enregistrement 
                        //result = new CustomerEntity(rs.getInt("CUSTOMER_ID"),rs.getString("name"),rs.getString("addressline1"));
                      
			
                        }
		}  catch (SQLException ex) {
			Logger.getLogger("DAO").log(Level.SEVERE, null, ex);
			throw new DAOException(ex.getMessage());
		}
                
                 return result;
	}

}
