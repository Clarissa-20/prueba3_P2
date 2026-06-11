/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prueba3_p2;

import java.util.Scanner;

/**
 *
 * @author HP
 */
public class Empresa {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner lea = new Scanner(System.in).useDelimiter("\n");
        EmpleadoManager mg = new EmpleadoManager();
        int opc = 0;

        do {
            try {
                System.out.println("\n******** MENU PRINCIPAL ********");
                System.out.println("1- Agregar empleado");
                System.out.println("2- Listar empleados NO despedidos");
                System.out.println("3- Agregar venta de empleado");
                System.out.println("4- Pagar empleado");
                System.out.println("5- Despedir empleado");
                System.out.println("6- Reporte completo de Empleado");
                System.out.println("7- Salir");
                System.out.print("Escoja una opcion: ");

                opc = lea.nextInt();

                switch (opc) {
                    case 1: 
                        System.out.print("Ingrese el nombre del empleado: ");
                        String nombre = lea.next();
                        System.out.print("Ingrese el salario: ");
                        double salario = lea.nextDouble();
                        mg.addEmployee(nombre, salario);
                        System.out.println("Empleado agregado con exito.");
                        break;

                    case 2: 
                        System.out.println("\n---- Lista de empleados activos ----");
                        mg.employeeList();
                        break;

                    case 3: 
                        System.out.print("Codigo del empleado: ");
                        int codigoVenta = lea.nextInt();
                        System.out.print("Monto de la venta: ");
                        double monto = lea.nextDouble();
                        mg.addSaleToEmployee(codigoVenta, monto);
                        break;

                    case 4:
                        System.out.print("Codigo del empleado a pagar: ");
                        int codigoPago = lea.nextInt();
                        mg.payEmployee(codigoPago);
                        break;

                    case 5: 
                        System.out.print("Codigo del empleado a despedir: ");
                        int codigoDespido = lea.nextInt();
                        mg.fireEmployee(codigoDespido);
                        break;

                    case 6:
                        System.out.print("Ingrese el codigo del empleado para el reporte: ");
                        int codigoReporte = lea.nextInt();
                        System.out.println("\n================ REPORTADO ================");
                        mg.printEmployee(codigoReporte);
                        System.out.println("===========================================");
                        break;
                        
                    case 7:
                        System.out.println("Saliendo del sistema :D");
                        break;

                    default:
                        System.out.println("Opcion invalida, intente de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: Verifique el codigo o el tipo de dato ingresado.");
                lea.next(); 
                opc = 0; 
            }

        } while (opc != 7);
    }
}
