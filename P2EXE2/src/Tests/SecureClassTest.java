package Tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exe2.SecureClass;

public class SecureClassTest {
	private class InterceptorOut extends PrintStream
	{
		private String text;
	    public InterceptorOut(OutputStream out)
	    {
	        super(out, true);
	        text = "";
	    }
	    @Override
	    public void print(String s)
	    {
	        super.print(s);
	        text = text + s;
	    }
	    
	    @Override
	    public void println(String s){
	    	super.println(s);
	    	//text = text + s + "\n";
	    }
	    
	    public String getText(){
	    	return text;
	    }
	    
	    public void eraseText(){
	    	text = "";
	    }
	}
	
	private String filename;
	private SecureClass sc;
	private InterceptorOut myPS;
	private PrintStream defaultPS;
	private InputStream myIS, defaultIS;
	
	private String msgArquivoIlegal = "Nome do arquivo ilegal";
	private String msgArquivoInexiste = "Arquivo não existe" ;
	private String msgPedirComando = "Digite a operacao desejada para realizar no arquivo <R para ler um arquivo, "
    		+ "W para escrever em um arquivo>? ";
	private String msgArquivoNaoEhLegivel = "Arquivo não é legível";
	private String msgArquivoNaoPodeSerEscrito = "Arquivo não pode ser escrito";
	private String msgEntradaInvalida = "Entrada inválida";
	private String msgPedirTexto = "Escreva algo: ";
	
	@Before
	public void setUp() throws Exception {
		File f = new File("Teste.txt");
		if(!f.exists()){
			f.createNewFile();
		}else{
			f.delete();
			f.createNewFile();
		}
		filename = "Teste.txt";
	    myPS = new InterceptorOut(System.out);
	    defaultPS = System.out;
	    defaultIS = System.in;
	    System.setOut(myPS);
	    sc = new SecureClass();
	}
	
	@After
	public void cleanUp(){
		System.setOut(defaultPS);
		System.setIn(defaultIS);
	}

	@Test
	public void testSetUp() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream(" R ".getBytes());
		System.setIn(ba);
		sc.vulnerableMethod(filename);
		assertEquals(msgPedirComando, myPS.getText());
	}

	@Test
	public void testIncorrectFileName() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream(" R ".getBytes());
		System.setIn(ba);
		sc.vulnerableMethod("Nome incorreto");
		assertEquals(msgArquivoInexiste, myPS.getText());
	}
	
	@Test
	public void testHackFileName() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream(" R ".getBytes());
		System.setIn(ba);
		sc.vulnerableMethod("../../dadosconfidenciais.txt");
		assertEquals(msgArquivoIlegal, myPS.getText());
	}
	
	@Test
	public void testIllegalInput() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream(" A ".getBytes());
		System.setIn(ba);
		sc.vulnerableMethod(filename);
		assertEquals(msgPedirComando+msgEntradaInvalida, myPS.getText());
	}
	
	@Test
	public void testIllegalFileRead() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream(" R ".getBytes());
		System.setIn(ba);
		File f = new File("NovoDirTeste");
		if(!f.exists()){
			f.mkdirs();
		}
		sc.vulnerableMethod("NovoDirTeste");
		assertEquals(msgPedirComando+msgArquivoNaoEhLegivel, myPS.getText());
	}
	
	@Test
	public void testIllegalFileWrite() throws IOException {
		ByteArrayInputStream ba;
		ba = new ByteArrayInputStream("W".getBytes());
		System.setIn(ba);
		File f = new File("NovoDirTeste");
		if(!f.exists()){
			f.mkdirs();
		}
		sc.vulnerableMethod("NovoDirTeste");
		assertEquals(msgPedirComando+msgArquivoNaoPodeSerEscrito, myPS.getText());
	}
	
	@Test
	public void testWrite() throws IOException {
		ByteArrayInputStream ba;
		StringBuilder sb = new StringBuilder();
		int i;
		for(i = 0; i <200; i++){
			sb.append("c");
		}
		ba = new ByteArrayInputStream((" W " + sb.toString()+"\n").getBytes());
		System.setIn(ba);
		sc.vulnerableMethod(filename);
		assertEquals(msgPedirComando + msgPedirTexto, myPS.getText());
		File f = new File(filename);
		FileReader fr = new FileReader(f);
		for(i = 0; fr.read()!=-1;i++);
		i--;
		assertEquals(100, i);
	}
	
	@Test
	public void testRead() throws IOException {
		ByteArrayInputStream ba;
		StringBuilder sb = new StringBuilder();
		String str;
		int i;
		for(i = 0; i <200; i++){
			sb.append("c");
		}
		str = sb.toString();
		File f = new File(filename);
		FileWriter fw = new FileWriter(f);
		fw.write(str);
		fw.flush();
		fw.close();
		ba = new ByteArrayInputStream((" R ").getBytes());
		System.setIn(ba);
		sc.vulnerableMethod(filename);
		assertEquals(msgPedirComando + str.substring(0, 100), myPS.getText());
	}
}
