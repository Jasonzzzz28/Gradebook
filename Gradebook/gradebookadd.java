

//import ...
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows the user to add a new student or assignment to a gradebook, or add a
 * grade for an existing student and existing assignment
 */
public class gradebookadd {

  /* parses the cmdline to keep main method simplified */
  private static String[] parse_cmdline(String[] args) {


    if (args.length >= 6) {


      String pattern = "(^-N [a-zA-Z0-9._]+ -K .+$)";
      Pattern reg = Pattern.compile(pattern);
      String front = args[0];
      for (int i = 1; i < 5; i++)
        front += " " + args[i];
      Matcher mc = reg.matcher(front);

      if (!mc.find()) {
        return null;

      } 

      String back = "";
      for (int j = 5; j < args.length; j++) {
        back += args[j] + " ";
      }

      String AN, P, W, FN, LN, G;
      switch (args[4]) {
      case "-AA":
        reg = Pattern.compile("(^(-AN [a-zA-Z0-9]+ |-P [0-9]+ |-W (0(.\\d+)?|1(.0+)?) )+$)");
        if (!reg.matcher(back).find()) {    
          return null;
        }

        AN = getArg("-AN ([a-zA-Z0-9]+) ", back);
        P = getArg("-P ([0-9]+) ", back);
        W = getArg("-W (0(?:.\\d+)?|1(?:.0+)?) ", back);

        if (AN == null || P == null || W == null) {
          return null;
        }

        return new String[] { args[1], args[3], "-AA", AN, P, W };
      case "-DA":
        reg = Pattern.compile("(^(-AN [a-zA-Z0-9]+ )+$)");
        if (!reg.matcher(back).find()) {
          return null;
        }

        AN = getArg("-AN ([a-zA-Z0-9]+) ", back);
        if (AN == null) {
          return null;
        }

        return new String[] { args[1], args[3], "-DA", AN };
      case "-AS":
        reg = Pattern.compile("(^(-FN [a-zA-Z]+ |-LN [a-zA-Z]+ )+$)");
        if (!reg.matcher(back).find()) {
          return null;
        }

        FN = getArg("-FN ([a-zA-Z]+) ", back);
        LN = getArg("-LN ([a-zA-Z]+) ", back);
        if (FN == null || LN == null) {
          return null;
        }

        return new String[] { args[1], args[3], "-AS", FN, LN };
      case "-DS":
        reg = Pattern.compile("(^(-FN [a-zA-Z]+ |-LN [a-zA-Z]+ )+$)");
        if (!reg.matcher(back).find()) {
          return null;
        }

        FN = getArg("-FN ([a-zA-Z]+) ", back);
        LN = getArg("-LN ([a-zA-Z]+) ", back);
        if (FN == null || LN == null) {
          return null;
        }

        return new String[] { args[1], args[3], "-DS", FN, LN };
      case "-AG":
        reg = Pattern.compile("(^(-FN [a-zA-Z]+ |-LN [a-zA-Z]+ |-AN [a-zA-Z0-9]+ |" + "-G [0-9]+ )+$)");
        if (!reg.matcher(back).find()) {
          return null;
        }

        FN = getArg("-FN ([a-zA-Z]+) ", back);
        LN = getArg("-LN ([a-zA-Z]+) ", back);
        AN = getArg("-AN ([a-zA-Z0-9]+) ", back);
        G = getArg("-G ([0-9]+) ", back);
        if (FN == null || LN == null || AN == null || G == null) {
          return null;
        }
        return new String[] { args[1], args[3], "-AG", FN, LN, AN, G };
      }

    }

    return null; 

    
    
  }

  private static void quit() {
    System.out.println("invalid");
    System.exit(255);
  }

  private static String getArg(String pattern, String source) {
    String res;
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(source);
    if (m.find())
      res = m.group(1);
    else
      return null;

    while (m.find())
      res = m.group(1);
    return res;
  }

  public static void main(String[] args) {

    String[] cmd = parse_cmdline(args);
    

    if (cmd == null) {
      quit();
    }
      

    //Check name and key and get the Gradebook
    Gradebook gradebook = null;
    try {
      gradebook = Helper.OpenGradeBook(cmd[0], cmd[1]);
    }
    catch (Exception e){
      quit();
    }

    //safety check
    if (gradebook == null) {
      quit();
    } 

    switch (cmd[2]) {
    case "-AA":
      gradebook.addAssignment(cmd[3], Integer.parseInt(cmd[4]), Double.parseDouble(cmd[5]));
      break;
    case "-DA":
      gradebook.delAssignment(cmd[3]);
      break;
    case "-AS":
      gradebook.addStudent(cmd[3], cmd[4]);
      break;
    case "-DS":
      gradebook.delStudent(cmd[3], cmd[4]);
      break;
    case "-AG":
      gradebook.addGrade(cmd[3], cmd[4], cmd[5], Integer.parseInt(cmd[6]));
      break;
    }

    try {
    	Helper.SaveGradebook(gradebook, cmd[0], cmd[1]);
    }
    catch (Exception e) {
      quit();
    }
  }
}