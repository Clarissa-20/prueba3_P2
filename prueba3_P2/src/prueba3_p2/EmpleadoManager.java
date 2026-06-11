/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba3_p2;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
/**
 *
 * @author HP
 */
public class EmpleadoManager {
    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File mf = new File("company");
            mf.mkdir();

            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");

            initCode();
            
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
    
    private void initCode() throws IOException{
        if(rcods.length()==0){
            rcods.writeInt(1);
        }
    }
    
    private int getCode() throws IOException{
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code+1);
        return code;
    } 
    
    public void addEmployee(String nombre, double salario)throws IOException{
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(nombre);
        remps.writeDouble(salario);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        
        createEmployeeFolder(code);
        
    }
    
    private String employeeFolder(int code){
        return "company/empleado.emp"+code;
    }
    
    private RandomAccessFile salesFileFor(int code) throws IOException{
        String dirPadre = employeeFolder(code); 
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre+"/ventas"+yearActual+".emp"; 
        return new RandomAccessFile(path, "rw");
    }
    
    private void createYearSaleFileFor(int code)throws IOException{
        RandomAccessFile ryear = salesFileFor(code);
        if (ryear.length()==0) { 
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0); 
                ryear.writeBoolean(false);
            }
        }
    }
    
    private void createEmployeeFolder(int code)throws IOException{ // NNNN
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSaleFileFor(code);
    }
    
    public void employeeList()throws IOException{ // NNNN
        remps.seek(0); 
        while(remps.getFilePointer()<remps.length()){
            int code = remps.readInt();
            String nombre = remps.readUTF();
            double salario = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            if(remps.readLong()==0){
                System.out.println(nombre+" | "+code+" | "+salario+": $ | "+fecha);
            }
        }
    } 
    
    private boolean isEmployeeActive(int code)throws IOException{
        remps.seek(0);
        while(remps.getFilePointer()<remps.length()){
            int codigo = remps.readInt();
            long pos = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);
            if(remps.readLong()==0 && codigo==code){
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
    
    public boolean fireEmployee(int code)throws IOException{
        if(isEmployeeActive(code)){
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: "+name);
            return true;
        }
        return false;
    }
    
    public void addSaleToEmployee(int code, double venta)throws IOException{
        if(isEmployeeActive(code)){
            RandomAccessFile sales = salesFileFor(code);
            int pos = Calendar.getInstance().get(Calendar.MONTH)*9;
            sales.seek(pos);
            double monto = sales.readDouble();
            sales.seek(pos);
            sales.writeDouble(monto+venta);
            System.out.println("Venta de: $"+venta+" agregada.");
        }
    }
    
    public RandomAccessFile billsFilefor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        String path = dirPadre+ "/recibos.emp";
        return new RandomAccessFile(path, "rw");
    }
    
     public boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile sales = salesFileFor(code);
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        int pos = mesActual * 9;
        sales.seek(pos);
        sales.skipBytes(8);
        return sales.readBoolean();
    }

     public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code) || isEmployeePayed(code)) {
            System.out.println("No se pudo pagar");
            return;
        }

        Calendar cal = Calendar.getInstance();
        int yearActual = cal.get(Calendar.YEAR);
        int mesActual = cal.get(Calendar.MONTH);
        String nombre = remps.readUTF();
        double salarioBase = remps.readDouble();

        RandomAccessFile sales = salesFileFor(code);
        int posMes = mesActual*9;
        sales.seek(posMes);
        double ventasMes = sales.readDouble();
        double sueldo = salarioBase+(ventasMes*0.10);
        double deduccion = sueldo*0.035;
        double totalApagar = sueldo-deduccion;

        RandomAccessFile bills = billsFilefor(code);
        bills.seek(bills.length()); 
        bills.writeLong(cal.getTimeInMillis()); 
        bills.writeDouble(sueldo);             
        bills.writeDouble(deduccion);        
        bills.writeInt(yearActual);            
        bills.writeInt(mesActual);             
        bills.close(); 

        sales.seek(posMes+8); 
        sales.writeBoolean(true);
        sales.close();
        System.out.println("Empleado "+nombre+" se le pago $"+totalApagar);
    }
     
     public void printEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("No se pudo pagar"); 
            return;
        }
        
        String nombre = remps.readUTF();
        double salario = remps.readDouble();
        Date fechaContratacion = new Date(remps.readLong());
        
        System.out.println("Codigo: "+code);
        System.out.println("Nombre: "+nombre);
        System.out.println("Salario: "+salario);
        
        Calendar calFecha = Calendar.getInstance();
        calFecha.setTime(fechaContratacion);
        System.out.printf("Fecha de contratacion: %02d/%02d/%d%n", 
                calFecha.get(Calendar.DAY_OF_MONTH), 
                (calFecha.get(Calendar.MONTH)+1), 
                calFecha.get(Calendar.YEAR));
        
        RandomAccessFile sales = salesFileFor(code);
        double totalVentasAnual = 0;
        
        for (int mes = 0; mes < 12; mes++) {
            sales.seek(mes*9);
            double ventasMes = sales.readDouble();
            totalVentasAnual += ventasMes;
            System.out.println("Mes "+(mes+1)+" : "+ventasMes);
        }
        sales.close();
        
        System.out.println("Total de ventas del año: "+totalVentasAnual);
        
        RandomAccessFile bills = billsFilefor(code);
        int contadorRecibos = 0;
        
        bills.seek(0);
        while (bills.getFilePointer() < bills.length()) {
            bills.skipBytes(32);
            contadorRecibos++;
        }
        bills.close();
        System.out.println("Total de pagos realizados: "+contadorRecibos);
    }
}
