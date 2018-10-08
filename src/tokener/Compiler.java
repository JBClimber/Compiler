package tokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Compiler {

	private  FileReader fr;
	private  BufferedReader br;
	private  FileWriter fwTok;
	public  BufferedWriter bwTok;
	private FileWriter fwAsm;
	public BufferedWriter bwAsm;
	private FileWriter fwIr;
	public BufferedWriter bwIr;
	// -- PRECOMPILER -----
	public FileReader pfr;
	public BufferedReader pbr;
	public FileWriter fwPC;
	public BufferedWriter bwPC;
	//----------------------
	
	
	String buffer;
	private  int statmentNo = 1;

	public int dataAdres = 0;
	
	public PreCompiler pComp = new PreCompiler(this);
	public Scanner s = new Scanner(this);
	public Parser p = new Parser(this);
	public IRcode ir = new IRcode(this);
	public Assembly as = new Assembly(this);
	
	public static void main (String[] args){
		
		Compiler app = new Compiler();
	}
	
	public Compiler(){
		System.out.println("Compiler ...... \n");
		
		try{
			//--  PreCOMPILER -----
			// only one file reader is required but two are used
			pfr = new FileReader("Program.sl");
			pbr = new BufferedReader(pfr);
			fwPC = new FileWriter("Program.pcm"); 
			bwPC = new BufferedWriter(fwPC);
			//--------------------
			fr = new FileReader("Program.pcm");
			br = new BufferedReader(fr);			// reads program file after pre compiler
			fwTok = new FileWriter("Program.tok");
			bwTok = new BufferedWriter(fwTok);		// writes to token file
			fwIr  = new FileWriter("Program.ir");
			bwIr  = new BufferedWriter(fwIr);		// writes to intermediate file
			fwAsm = new FileWriter("Program.asm");
			bwAsm = new BufferedWriter(fwAsm);		// writes to assembly file
						
			//----- PRE COMPILER --------------------
			// precompiler translates FOR loop into WHILE loop
			while ( (buffer = pbr.readLine()) != null){
				String bufferCopy = buffer;
				s.scanner();
				if (!buffer.isEmpty()){
					System.out.println("buffer:"+buffer);
					pComp.preCompiler(bufferCopy);
				}
			s.clearTokens();
			}
			pbr.close();
			bwPC.close();
			//------END PRE COMPILER -------------------

			String err = "";

			while ( (buffer = br.readLine()) != null ){
				Errors e = new Errors();
				System.out.print("Statement "+statmentNo+" : "+ buffer +"\n");
				bwTok.write("Statement "+statmentNo+" : "+ buffer +"\n");
				if ( !buffer.isEmpty() ){				// prevents empty strings to enter the scanner
					s.scanner();
					err = e.errorList(s.tokens);		// gets errors
					if ( !buffer.isEmpty() ){
						p.parser();
					}
					s.printTokens();	// error checking
					System.out.print(buffer);
					bwTok.write(buffer);
					
					if (!err.isEmpty())					// does not print out if string = ""
						System.out.println(err);
				}
				System.out.print("---------------------\n");
				bwTok.write(err);
				bwTok.write("---------------------\n");
				statmentNo++;
				bwTok.flush();
				s.clearTokens();	// clears tokens for the new buffer in Scanner
				p.clearParse();		// clears parse string
			}
			System.out.print("End Of Process");
			bwTok.write("End Of Process");
			
			br.close();
			bwTok.close();
			bwAsm.close();
		}
		catch (FileNotFoundException e){
			System.out.println("File NOT found "+e);
		}
		catch (IOException e) {
			System.out.println("IO problems "+e);
		}
		System.exit(0);
	}

}
