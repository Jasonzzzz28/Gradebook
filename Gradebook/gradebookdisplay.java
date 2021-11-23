/**
* Prints out a gradebook in a few ways
* Some skeleton functions are included
*/
public class gradebookdisplay {


public static void main(String[] args) {

  if(args.length>=6)
  {

      boolean nameSym = args[0].equals("-N");
      boolean keySym = args[2].equals("-K");
      
      boolean nameKeyCheck = false;
      
      
      Gradebook gradebook = null;
      
      try {
        gradebook = Helper.OpenGradeBook(args[1], args[3]);
      }
      catch (Exception e){
        System.out.println("invalid");
        System.exit(255);
      }

      
      if (gradebook != null) {
        nameKeyCheck = true; 
      }

      boolean actionSym = args[4].equals("-PA") || args[4].equals("-PS") || args[4].equals("-PF");

      if(nameSym == false || keySym == false || nameKeyCheck == false || actionSym == false) {
        System.out.println("invalid");
        System.exit(255);
      }

      if(args[4].equals("-PA")) {

        boolean precond = false;
        boolean cond1 = false;
        String cond1Val = null;

        boolean cond2 = false;
        String cond2Val = null; 

        for(int i = 5; i < args.length; i++) {

            if(precond == true) {

                if(args[i].matches("[a-zA-Z0-9]+")) {
                  cond1 = true;
                  cond1Val = args[i];
                  precond = false;
                  continue;
                }

                else {
                  System.out.println("invalid");
                  System.exit(255);

                }
                
            }


            if(args[i].equals("-AN")) {
              precond = true;
              continue;
            }

            if(args[i].equals("-A")) {

              if(cond2Val != null && !cond2Val.equals(args[i])) {
                System.out.println("invalid");
                System.exit(255);
              }

              else {
                cond2 = true;
                cond2Val = args[i];
                continue;
              }

              
            }

            if(args[i].equals("-G")) {

              if(cond2Val != null && !cond2Val.equals(args[i])) {
                System.out.println("invalid");
                System.exit(255);
              }

              else {
                cond2 = true;
                cond2Val = args[i];
                continue;
              }

              
            }

            else {
              System.out.println("invalid");
              System.exit(255);
            }

        }

        if(cond1 == true && cond2 == true) {
          
          if(cond2Val.equals("-A")) {
            gradebook.printAssignmentA(cond1Val);
          }
          
          else if(cond2Val.equals("-G")) {
            gradebook.printAssignmentG(cond1Val);
          }
          
          
        }

        else {
          System.out.println("invalid");
          System.exit(255);
        }


      }


      else if(args[4].equals("-PS")) {

        boolean precond1 = false;
        boolean cond1 = false;
        String cond1Val = null;

        boolean precond2 = false;
        boolean cond2 = false;
        String cond2Val = null;

        for(int i = 5; i < args.length; i++) {

          if(precond1 == true) {

                if(args[i].matches("[a-zA-Z]+")) {
                  cond1 = true;
                  cond1Val = args[i];
                  precond1 = false;
                  continue;
                }

                else {
                  System.out.println("invalid");
                  System.exit(255);
                }
                
          }


          if(precond2 == true) {

                if(args[i].matches("[a-zA-Z]+")) {
                  cond2 = true;
                  cond2Val = args[i];
                  precond2 = false;
                  continue;
                }

                else {
                  System.out.println("invalid");
                  System.exit(255);
                }
                
          }


          if(args[i].equals("-FN")) {
              precond1 = true;
              continue;
          }

          if(args[i].equals("-LN")) {
              precond2 = true;
              continue;
          }

          else {
              System.out.println("invalid");
              System.exit(255);
          }


        }

        if(cond1 == true && cond2 == true) {
          
          gradebook.printStudent(cond1Val, cond2Val);

        }

        else {
          System.out.println("invalid");
          System.exit(255);
        }


      }

      else if(args[4].equals("-PF")) {

        boolean cond = false;
        String condVal = null;

        for(int i = 5; i < args.length; i++) {

          if(args[i].equals("-A")) {

              if(condVal != null && !condVal.equals(args[i])) {
                System.out.println("invalid");
                System.exit(255);   
              }

              else {
                cond = true;
                condVal = args[i];
                continue;
              }

              
          }


        if(args[i].equals("-G")) {

              if(condVal != null && !condVal.equals(args[i])) {
                System.out.println("invalid");
                System.exit(255);
              }

              else {
                cond = true;
                condVal = args[i];
                continue;
              }

              
        }

        else {
            System.out.println("invalid");
            System.exit(255);
        }


      }

      if(cond == true) {
        
        if(condVal.equals("-A")) {
          gradebook.printFinalA();
        }
        
        else if(condVal.equals("-G")) {
          gradebook.printFinalG();
        }
        
      }

      else {
        System.out.println("invalid");
        System.exit(255);
      }

    }
      
      
      try {
          Helper.SaveGradebook(gradebook, args[1], args[3]);
      }
      
      catch (Exception e) {
          System.out.println("invalid");
          System.exit(255);
      }

  }
  
  else {
    System.out.println("invalid");
      System.exit(255);
  }

 }

}
