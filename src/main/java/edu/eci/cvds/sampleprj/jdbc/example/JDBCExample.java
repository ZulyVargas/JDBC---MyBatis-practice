/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.cvds.sampleprj.jdbc.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="prueba2019";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            /**
            int suCodigoECI = 2163297;
            registrarNuevoProducto(con, suCodigoECI, "Zuly Vargas", 19999999);            
            **/
            con.commit();
            con.close();
            
 
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
    	String insert = "INSERT INTO ORD_PRODUCTOS(codigo,nombre,precio) VALUES(" + Integer.toString(codigo) +", '"+ nombre + "',"+ precio + ")";
    	try( PreparedStatement preparedStatement = con.prepareStatement(insert); ){
			//usar executeQuery
    		 con.setAutoCommit(false);
	         preparedStatement.execute();
	         con.commit();
		} catch (SQLException e) {
			try {
				  e.printStackTrace();
				  System.err.print("Transaction is being rolled back");
		          con.rollback();
		        } catch (SQLException excep) {
		        	e.printStackTrace();
		        }
		}
         
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido){
        List<String> np=new LinkedList<>();
        //Crear prepared statement
        String select = "SELECT nombre FROM ORD_PRODUCTOS JOIN ORD_DETALLE_PEDIDO ON ORD_DETALLE_PEDIDO.producto_fk = ORD_PRODUCTOS.codigo JOIN ORD_PEDIDOS ON ORD_DETALLE_PEDIDO.pedido_fk =  ORD_PEDIDOS.codigo WHERE ORD_PEDIDOS.codigo = " + Integer.toString(codigoPedido); 
        //asignar parámetros
        try( PreparedStatement preparedStatement = con.prepareStatement(select); ){
			//usar executeQuery
	        ResultSet resultSet = preparedStatement.executeQuery();
	      //Sacar resultados del ResultSet
	      //Llenar la lista y retornarla
	        while(resultSet.next()) {
	        	np.add(resultSet.getString("nombre"));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido){
    	int resultado = 0;
    	//Crear prepared statement
    	String select = "SELECT SUM(ORD_PRODUCTOS.precio * ORD_DETALLE_PEDIDO.cantidad) AS total FROM ORD_PRODUCTOS JOIN ORD_DETALLE_PEDIDO ON ORD_PRODUCTOS.codigo = producto_fk JOIN ORD_PEDIDOS ON ORD_DETALLE_PEDIDO.pedido_fk = ORD_PEDIDOS.codigo WHERE ORD_PEDIDOS.codigo = " + Integer.toString(codigoPedido);
    	
    	try( PreparedStatement preparedStatement = con.prepareStatement(select); ){
			//usar executeQuery
	        ResultSet resultSet = preparedStatement.executeQuery();
	      //Sacar resultados del ResultSet
	        if (resultSet.next()) resultado = resultSet.getInt("total");
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return resultado;
    }
    

    
    
    
}