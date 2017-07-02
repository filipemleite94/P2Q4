package exe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SecureClass {
  /**
  * Secure method
  * @param fileName Name of the archive.
  */
  public void vulnerableMethod(String fileName) {

    final int max = 1;
    final int maxChar = 100;
    int iter;
    iter = 0;
    if (fileName.contains("..")) {
      System.out.println("Nome do arquivo ilegal");
      return;
    }
    File file;
    file = new File(fileName);
    if (!file.exists()) {
      System.out.println("Arquivo não existe");
      return;
    }
    while (iter++ < max) {
      Scanner console = new Scanner(System.in);
      System.out.print("Digite a operacao desejada para realizar no arquivo");
      System.out.println("<R para ler um arquivo, " + "W para escrever em um arquivo>? ");

      String opr;

      if (console.hasNext("[RW]")) {
        opr = console.next("[RW]");
      } else {
        System.out.println("Entrada inválida");
        return;
      }
      if (opr.equals("R")) {
        FileReader fr = null;
        StringBuilder sb = null;
        char carac = 0;
        int intcarac;
        int jterator = 0;
        try {
          fr = new FileReader(file);
          sb = new StringBuilder();
          while ((jterator++ < maxChar) && (intcarac = fr.read()) != -1) {
            sb.append((char)intcarac);
          }
          System.out.println(sb.toString());
          fr.close();
        } catch (IOException e) {
          System.out.println("Arquivo não é legível");
        } 
      } else {
        FileWriter fw;
        BufferedWriter buffWrite;
        try {
          fw = new FileWriter(file);
          buffWrite = new BufferedWriter(fw);
          String linha = "";
          System.out.println("Escreva algo: ");
          console.nextLine();
          if (console.hasNextLine()) {
            linha = console.nextLine();
          }
          if (linha.length() > maxChar) {
            linha = linha.substring(0, maxChar);
          }
          buffWrite.append(linha + "\n");
          buffWrite.flush();
          buffWrite.close();
        } catch (IOException e) {
          System.out.println("Arquivo não pode ser escrito");
        }
      }
      console.close();
    }
  }
}
