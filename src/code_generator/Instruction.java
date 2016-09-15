package code_generator;

public class Instruction {
	
	private int instType;
	
	// Load and store instructions
	public static final int LDO = 0;
	public static final int LDC = 1;
	public static final int IND = 2;
	public static final int SRO = 3;
	public static final int STO = 4;
	public static final int LOD = 5;
	public static final int LDA = 6;
	public static final int STR = 7;
	
	// Arithmetic and logical instructions
	public static final int OR = 8;
	public static final int AND = 9;
	public static final int EQU = 10;
	public static final int NEQ = 11;
	public static final int LES = 12;
	public static final int GRT = 13;
	public static final int LEQ = 14;
	public static final int GEQ = 15;
	public static final int ADD = 16;
	public static final int SUB = 17;
	public static final int MUL = 18;
	public static final int DIV = 19;
	public static final int MOD = 20;
	public static final int NOT = 21;
	public static final int NEG = 42;

	// Jump instructions
	public static final int UJP = 22;
	public static final int FJP = 23;
	public static final int IXJ = 24;
	public static final int CUP = 25;
	
	// Access to arrays and to the heap
	public static final int IXA = 26;
	public static final int CHK = 27;
	public static final int INC = 28;
	public static final int DEC = 29;
	public static final int DPL = 30;
	public static final int LDD = 31;
	public static final int SLI = 32;
	public static final int NEW = 33;
	
	// Methods, void methods and main program
	public static final int MOVS = 34;
	public static final int MOVD = 35;
	public static final int MST = 36;
	public static final int SSP = 37;
	public static final int SEP = 38;
	public static final int RETF = 39;
	public static final int RETP = 40;
	public static final int STP = 41;
	
	public Instruction(int instType) {
		this.instType = instType;
	}
	
	public String toString(int currDepth, String value) {
		String instStr; 
		switch(this.instType) {
		// Load and store instructions
		case LDO:
			instStr = "ldo " + value + ";\n";
		break;
		case LDC:
			instStr = "ldc " + value + ";\n";
		break;
		case IND:
			instStr = "ind" + ";\n";
		break;
		case SRO:
			instStr = "sro " + value + ";\n";
		break;
		case STO:
			instStr = "sto" + ";\n";
		break;
		case LOD:
			instStr = "lod " + currDepth + " " + value + ";\n";
		break;
		case LDA:
			instStr = "lda " + currDepth + " " + value + ";\n";
		break;
		case STR:
			instStr = "str " + "0" + " " + "0" + ";\n";
		break;
		
		// Arithmetic and logical instructions
		case ADD:
			instStr = "add" + ";\n";
		break;
		case SUB:
			instStr = "sub" + ";\n";
		break;
		case MUL:
			instStr = "mul" + ";\n";
		break;
		case DIV:
			instStr = "div" + ";\n";
		break;
		case NEG:
			instStr = "neg" + ";\n";
		break;
		case AND:
			instStr = "and" + ";\n";
		break;
		case OR:
			instStr = "or" + ";\n";
		break;
		case NOT:
			instStr = "not" + ";\n";
		break;
		case EQU:
			instStr = "equ" + ";\n";
		break;
		case GEQ:
			instStr = "geq" + ";\n";
		break;
		case LEQ:
			instStr = "leq" + ";\n";
		break;
		case LES:
			instStr = "les" + ";\n";
		break;
		case GRT:
			instStr = "grt" + ";\n";
		break;
		case NEQ:
			instStr = "neq" + ";\n";
		break;
		case MOD: // NOTE: Currently the module operation is not suported by the p-machine
			instStr = "mod" + ";\n";
		break;
		
		// Jump instructions
		case UJP:
			instStr = "ujp ";
		break;
		case FJP:
			instStr = "fjp ";
		break;
		case IXJ:
			instStr = "ixj ";
		break;
		case CUP:
			instStr = "cup " + currDepth + " " + value + ";\n";
		break;
		
		// Access to arrays and to the heap
		case IXA:
			instStr = "ixa " + value + ";\n";
		break;
		case CHK:
			instStr = "chk " + currDepth + " " + value + ";\n";
		break;
		case INC:
			instStr = "inc " + value + ";\n";
		break;
		case DEC:
			instStr = "dec " + value + ";\n";
		break;
		case DPL:
			instStr = "dpl" + ";\n";
		break;
		case LDD:
			instStr = "ldd " + value + ";\n";
		break;
		case SLI:
			instStr = "sli" + ";\n";
		break;
		case NEW:
			instStr = "new" + ";\n";
		break;
		
		// Methods, void methods and main program
		case MOVS:
			instStr = "movs " + value + ";\n";
		break;
		case MOVD:
			instStr = "movd " + value + ";\n";
		break;
		case MST:
			instStr = "mst " + value + ";\n";
		break;
		case SSP:
			instStr = "ssp " + value + ";\n";
		break;
		case SEP:
			instStr = "sep " + value + ";\n";
		break;
		case RETF:
			instStr = "retf" + ";\n";
		break;
		case RETP:
			instStr = "retp" + ";\n";
		break;
		case STP:
			instStr = "stp" + ";\n";
		break;
		default: 
			instStr = "" + ";\n";
		}
		return instStr;
	}
	
}
