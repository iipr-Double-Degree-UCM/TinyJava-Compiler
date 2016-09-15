package AA_Main;

import java.io.Reader;

import code_generator.CodeManager;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ScannerBuffer;
import lexical_analyzer.LexicalAnalyzerTiny;
import semantic_analyzer.IdAndTypeChecker;
import syntactic_analyzer.SyntacticAnalyzerTiny;
import syntactic_analyzer.syntax.Program;
import error_handle.Errors;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, Exception {
		Reader input = new InputStreamReader(new FileInputStream("./examples/input.txt"));
		ComplexSymbolFactory csf = new ComplexSymbolFactory();
		ScannerBuffer lexA = new ScannerBuffer(new LexicalAnalyzerTiny(input, csf));
		SyntacticAnalyzerTiny synA = new SyntacticAnalyzerTiny(lexA, csf);
		Program p = (Program)synA.parse().value;
		IdAndTypeChecker idTypeChecker = new IdAndTypeChecker();
		try {
			p.check(idTypeChecker);
			CodeManager coder = new CodeManager();
			p.code(coder);
			outputCode2File(coder.getCode());
		} catch (Errors e) {
			e.print();
		}
		
		@SuppressWarnings("unused")
		int i = 0;
		
	}
	
	private static void outputCode2File(String strOut) {
		try {
			PrintWriter file = new PrintWriter("./examples/output.txt");
			file.print(strOut);
			file.flush();
			file.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}
