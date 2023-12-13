package Consultas;

import Conexion.ConexionMariaDB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.*;

public class ListarMaquinaria {

    public static void listar() {

        PreparedStatement ps;
        ResultSet rs;

        try (Connection conexion = ConexionMariaDB.conectar("constructora")) {
            try (Statement state = conexion.createStatement()) {

                ResultSet resultSet = state.executeQuery("SELECT * FROM maquinaria m, empleado e WHERE m.empleado=e.dni");
                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    String obra = resultSet.getString("nombreObra");

                    System.out.println("Nombre empleado: " + nombre + "\tNombre obra asignada: " + obra);
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLSyntaxErrorException e) {
            System.out.println("Error en la sintaxis de la sentencia SQL: " + e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("La sentencia SQL no cumple con los requisitos de integridad de la base de datos: " + e.getMessage());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}