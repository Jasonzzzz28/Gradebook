
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.javatuples.Triplet;

/**
 * A helper class for your gradebook Some of these methods may be useful for
 * your program You can remove methods you do not need If you do not wiish to
 * use a Gradebook object, don't
 */
public class Gradebook {
  JsonObject gradebook;
  String name;
  int numStudents;
  int numAssignments;
  double totalWeight;

  /* Create a new gradebook with filename GBName */
  public Gradebook(String GBName) {
    JsonArray assign = new JsonArray();
    JsonArray students = new JsonArray();

    gradebook = new JsonObject();
    gradebook.addProperty("name", GBName);
    gradebook.add("assignments", assign);
    gradebook.add("students", students);

    name = GBName;
    numStudents = 0;
    numAssignments = 0;
    totalWeight = 0.0;
  }

  /* Create a new gradebook from given Json string */
  public Gradebook(String json, boolean isJson) {

    try {

      JsonParser parser = new JsonParser();
      gradebook = (JsonObject) parser.parse(json);
      name = gradebook.get("name").getAsString();
      numStudents = gradebook.get("students").getAsJsonArray().size();
      numAssignments = gradebook.get("assignments").getAsJsonArray().size();
      totalWeight = 0.0;
      Iterator<JsonElement> iterator = gradebook.get("assignments").getAsJsonArray().iterator();


    } catch(Exception e) {
      System.out.println(e.toString());
      throw e; 

    }


    
  }

  /* return the size of the gradebook */
  public int size(int lenType) {
    if (lenType == 1) {
      return numStudents;
    } else if (lenType == 2) {
      return numAssignments;
    } else {
      return -1;
    }
  }

  /* Adds a student to the gradebook */
  public void addStudent(String fName, String lName) {
    if (hasProperty(fName + lName, gradebook.get("students").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    } else {
      JsonArray grades = new JsonArray();

      JsonObject student = new JsonObject();
      student.addProperty("firstname", fName);
      student.addProperty("lastname", lName);
      student.add("grades", grades);

      JsonObject studentID = new JsonObject();
      studentID.add(fName + lName, student);
      // duplicates?
      gradebook.get("students").getAsJsonArray().add(studentID);

      numStudents++;
    }
  }

  /* Removes a student from the gradebook */
  public void delStudent(String fName, String lName) {
    // duplicate names?
    int firstCheck = gradebook.get("students").getAsJsonArray().size();
    Iterator<JsonElement> iterator = gradebook.get("students").getAsJsonArray().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getAsJsonObject().has(fName + lName)) {
        iterator.remove();
        numStudents--;
      }
    }
    int secondCheck = gradebook.get("students").getAsJsonArray().size();

    if (firstCheck == secondCheck) {
      System.out.println("invalid");
      System.exit(255);
    }

  }

  /* Adds an assinment to the gradebook */
  public void addAssignment(String assName, int points, double weight) {
    if (totalWeight + weight > 1.0) {
      System.out.println("invalid");
      System.exit(255);
    } else if (hasProperty(assName, gradebook.get("assignments").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    } else {
      JsonObject assign = new JsonObject();
      assign.addProperty("name", assName);
      assign.addProperty("points", points);
      assign.addProperty("weight", weight);

      JsonObject assignID = new JsonObject();
      assignID.add(assName, assign);
      // duplicates?
      gradebook.get("assignments").getAsJsonArray().add(assignID);

      Iterator<JsonElement> iterator = gradebook.get("students").getAsJsonArray().iterator();
      while (iterator.hasNext()) {
        JsonElement student = iterator.next();
        Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
        String name = "";
        while (nameIter.hasNext()) {
          name = nameIter.next();
        }
        String fName = student.getAsJsonObject().get(name).getAsJsonObject().get("firstname").getAsString();
        String lName = student.getAsJsonObject().get(name).getAsJsonObject().get("lastname").getAsString();
        addGrade(fName, lName, assName, 0);
      }

      numAssignments++;
    }
  }

  /* Removes an assinment from the gradebook */
  public void delAssignment(String assName) {
    int firstCheck = gradebook.get("assignments").getAsJsonArray().size();
    Iterator<JsonElement> iterator = gradebook.get("assignments").getAsJsonArray().iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getAsJsonObject().has(assName)) {
        iterator.remove();
        numAssignments--;
      }
    }
    int secondCheck = gradebook.get("assignments").getAsJsonArray().size();

    if (firstCheck == secondCheck) {
      System.out.println("invalid");
      System.exit(255);
    }

    Iterator<JsonElement> iteratorStudents = gradebook.get("students").getAsJsonArray().iterator();
    while (iteratorStudents.hasNext()) {
      JsonObject student = iteratorStudents.next().getAsJsonObject();
      Iterator<String> nameIter = student.keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      Iterator<JsonElement> iteratorGrades = student.get(name).getAsJsonObject().get("grades").getAsJsonArray()
          .iterator();
      while (iteratorGrades.hasNext()) {
        if (iteratorGrades.next().getAsJsonObject().has(assName)) {
          iteratorGrades.remove();
        }
      }
    }

  }

  /* Adds a grade to the gradebook */
  public void addGrade(String fName, String lName, String assName, int grade) {
    if (!hasProperty(fName + lName, gradebook.get("students").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    } else if (!hasProperty(assName, gradebook.get("assignments").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    } else {
      JsonObject gradeOb = new JsonObject();
      gradeOb.addProperty("assignment", assName);
      gradeOb.addProperty("grade", grade);

      JsonObject gradeObId = new JsonObject();
      gradeObId.add(assName, gradeOb);

      Iterator<JsonElement> iterator = gradebook.get("students").getAsJsonArray().iterator();
      while (iterator.hasNext()) {
        JsonElement student = iterator.next();
        if (student.getAsJsonObject().has(fName + lName)) {
          Iterator<JsonElement> iteratorGrades = student.getAsJsonObject().get(fName + lName).getAsJsonObject()
              .get("grades").getAsJsonArray().iterator();
          while (iteratorGrades.hasNext()) {
            if (iteratorGrades.next().getAsJsonObject().has(assName)) {
              iteratorGrades.remove();
            }
          }

          student.getAsJsonObject().get(fName + lName).getAsJsonObject().get("grades").getAsJsonArray().add(gradeObId);
        }
      }
    }

  }

  public void printAssignmentA(String assName) {



    if (!hasProperty(assName, gradebook.get("assignments").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    }
    ArrayList<Triplet<String, String, Integer>> grades = new ArrayList<Triplet<String, String, Integer>>();

    Iterator<JsonElement> iterator = gradebook.get("students").getAsJsonArray().iterator();
    while (iterator.hasNext()) {
      JsonElement student = iterator.next();
      Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      String fName = student.getAsJsonObject().get(name).getAsJsonObject().get("firstname").getAsString();
      String lName = student.getAsJsonObject().get(name).getAsJsonObject().get("lastname").getAsString();
      Iterator<JsonElement> iteratorGrades = student.getAsJsonObject().get(name).getAsJsonObject().get("grades")
          .getAsJsonArray().iterator();
      while (iteratorGrades.hasNext()) {
        JsonObject grade = iteratorGrades.next().getAsJsonObject();
        if (grade.has(assName)) {
          int points = grade.get(assName).getAsJsonObject().get("grade").getAsInt();
          Triplet<String, String, Integer> triplet = Triplet.with(lName, fName, points);
          grades.add(triplet);
        }
      }
    }

    Collections.sort(grades, new Comparator<Triplet<String, String, Integer>>() {
      @Override
      public int compare(Triplet<String, String, Integer> tup1, Triplet<String, String, Integer> tup2) {
        if (tup1.getValue0().compareTo(tup2.getValue0()) == 0) {
          return tup1.getValue1().compareTo(tup2.getValue1());
        } else {
          return tup1.getValue0().compareTo(tup2.getValue0());
        }
      }
    });

    grades.forEach((tup) -> System.out.println(tup));
  }

  public void printAssignmentG(String assName) {
    if (!hasProperty(assName, gradebook.get("assignments").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    }
    ArrayList<Triplet<String, String, Integer>> grades = new ArrayList<Triplet<String, String, Integer>>();

    Iterator<JsonElement> iterator = gradebook.get("students").getAsJsonArray().iterator();
    while (iterator.hasNext()) {
      JsonElement student = iterator.next();
      Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      String fName = student.getAsJsonObject().get(name).getAsJsonObject().get("firstname").getAsString();
      String lName = student.getAsJsonObject().get(name).getAsJsonObject().get("lastname").getAsString();
      Iterator<JsonElement> iteratorGrades = student.getAsJsonObject().get(name).getAsJsonObject().get("grades")
          .getAsJsonArray().iterator();
      while (iteratorGrades.hasNext()) {
        JsonObject grade = iteratorGrades.next().getAsJsonObject();
        if (grade.has(assName)) {
          int points = grade.get(assName).getAsJsonObject().get("grade").getAsInt();
          Triplet<String, String, Integer> triplet = Triplet.with(lName, fName, points);
          grades.add(triplet);
        }
      }
    }

    Collections.sort(grades, new Comparator<Triplet<String, String, Integer>>() {
      @Override
      public int compare(Triplet<String, String, Integer> tup1, Triplet<String, String, Integer> tup2) {
        return tup2.getValue2().compareTo(tup1.getValue2());
      }
    });

    grades.forEach((tup) -> System.out.println(tup));
  }

  public void printStudent(String fName, String lName) {
    if (!hasProperty(fName + lName, gradebook.get("students").getAsJsonArray())) {
      System.out.println("invalid");
      System.exit(255);
    }
    ArrayList<Triplet<String, String, Integer>> grades = new ArrayList<Triplet<String, String, Integer>>();
    ArrayList<Triplet<String, String, Integer>> zeros = new ArrayList<Triplet<String, String, Integer>>();

    Iterator<JsonElement> iteratorStudent = gradebook.get("students").getAsJsonArray().iterator();
    while (iteratorStudent.hasNext()) {
      JsonObject student = iteratorStudent.next().getAsJsonObject();
      Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      String studentName = student.get(name).getAsJsonObject().get("firstname").getAsString()
          + student.get(name).getAsJsonObject().get("lastname").getAsString();
      if (studentName.compareTo(fName + lName) == 0) {
        Iterator<JsonElement> iteratorGrades = student.get(fName + lName).getAsJsonObject().get("grades")
            .getAsJsonArray().iterator();
        while (iteratorGrades.hasNext()) {
          JsonObject grade = iteratorGrades.next().getAsJsonObject();
          Iterator<String> gradeIter = grade.getAsJsonObject().keySet().iterator();
          String assName = "";
          while (gradeIter.hasNext()) {
            assName = gradeIter.next();
          }
          if (grade.getAsJsonObject().get(assName).getAsJsonObject().get("grade").getAsInt() == 0) {
            int points = grade.getAsJsonObject().get(assName).getAsJsonObject().get("grade").getAsInt();
            Triplet<String, String, Integer> triplet = Triplet.with(lName, fName, points);
            zeros.add(triplet);
          } else {
            int points = grade.getAsJsonObject().get(assName).getAsJsonObject().get("grade").getAsInt();
            Triplet<String, String, Integer> triplet = Triplet.with(lName, fName, points);
            grades.add(triplet);
          }
        }
      }
    }

    // Possible do it in reverse order?
    grades.forEach((tup) -> System.out.println(tup));
    zeros.forEach((tup) -> System.out.println(tup));
  }

  public void printFinalA() {
    ArrayList<Triplet<String, String, Double>> grades = new ArrayList<Triplet<String, String, Double>>();
    Double finalGrade = 0.0;

    Iterator<JsonElement> iteratorStudent = gradebook.get("students").getAsJsonArray().iterator();
    while (iteratorStudent.hasNext()) {
      JsonObject student = iteratorStudent.next().getAsJsonObject();
      Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      String fName = student.get(name).getAsJsonObject().get("firstname").getAsString();
      String lName = student.get(name).getAsJsonObject().get("lastname").getAsString();
      String studentName = fName + lName;
      Iterator<JsonElement> iteratorGrades = student.get(studentName).getAsJsonObject().get("grades").getAsJsonArray()
          .iterator();
      while (iteratorGrades.hasNext()) {
        JsonObject grade = iteratorGrades.next().getAsJsonObject();
        Iterator<String> gradeIter = grade.getAsJsonObject().keySet().iterator();
        String assName = "";
        while (gradeIter.hasNext()) {
          assName = gradeIter.next();
        }
        Iterator<JsonElement> assIterator = gradebook.get("assignments").getAsJsonArray().iterator();
        while (assIterator.hasNext()) {
          JsonObject assignment = assIterator.next().getAsJsonObject();
          Iterator<String> assIterName = assignment.getAsJsonObject().keySet().iterator();
          String assNamenew = "";
          while (assIterName.hasNext()) {
            assNamenew = assIterName.next();
          }
          if (assignment.get(assNamenew).getAsJsonObject().get("name").getAsString().compareTo(assName) == 0) {

            Double score = grade.getAsJsonObject().get(assName).getAsJsonObject().get("grade").getAsDouble()/assignment.get(assName).getAsJsonObject().get("points").getAsInt();

            finalGrade += score * assignment.get(assName).getAsJsonObject().get("weight").getAsDouble();
          }
        }
      }
      Triplet<String, String, Double> triplet = Triplet.with(lName, fName, finalGrade);
      grades.add(triplet);
      finalGrade = 0.0;
    }

    Collections.sort(grades, new Comparator<Triplet<String, String, Double>>() {
      @Override
      public int compare(Triplet<String, String, Double> tup1, Triplet<String, String, Double> tup2) {
        if (tup1.getValue0().compareTo(tup2.getValue0()) == 0) {
          return tup1.getValue1().compareTo(tup2.getValue1());
        } else {
          return tup1.getValue0().compareTo(tup2.getValue0());
        }
      }
    });

    grades.forEach((tup) -> System.out.println(tup));
  }

  public void printFinalG() {
    ArrayList<Triplet<String, String, Double>> grades = new ArrayList<Triplet<String, String, Double>>();
    Double finalGrade = 0.0;

    Iterator<JsonElement> iteratorStudent = gradebook.get("students").getAsJsonArray().iterator();
    while (iteratorStudent.hasNext()) {
      JsonObject student = iteratorStudent.next().getAsJsonObject();
      Iterator<String> nameIter = student.getAsJsonObject().keySet().iterator();
      String name = "";
      while (nameIter.hasNext()) {
        name = nameIter.next();
      }
      String fName = student.get(name).getAsJsonObject().get("firstname").getAsString();
      String lName = student.get(name).getAsJsonObject().get("lastname").getAsString();
      String studentName = fName + lName;
      Iterator<JsonElement> iteratorGrades = student.get(studentName).getAsJsonObject().get("grades").getAsJsonArray()
          .iterator();
      while (iteratorGrades.hasNext()) {
        JsonObject grade = iteratorGrades.next().getAsJsonObject();
        Iterator<String> gradeIter = grade.getAsJsonObject().keySet().iterator();
        String assName = "";
        while (gradeIter.hasNext()) {
          assName = gradeIter.next();
        }
        Iterator<JsonElement> assIterator = gradebook.get("assignments").getAsJsonArray().iterator();
        while (assIterator.hasNext()) {
          JsonObject assignment = assIterator.next().getAsJsonObject();
          Iterator<String> assIterName = assignment.getAsJsonObject().keySet().iterator();
          String assNamenew = "";
          while (assIterName.hasNext()) {
            assNamenew = assIterName.next();
          }
          if (assignment.get(assNamenew).getAsJsonObject().get("name").getAsString().compareTo(assName) == 0) {
            Double score = grade.getAsJsonObject().get(assName).getAsJsonObject().get("grade").getAsDouble()/assignment.get(assName).getAsJsonObject().get("points").getAsInt();

            finalGrade += score * assignment.get(assName).getAsJsonObject().get("weight").getAsDouble();
          }
        }
      }
      Triplet<String, String, Double> triplet = Triplet.with(lName, fName, finalGrade);
      grades.add(triplet);
      finalGrade = 0.0;
    }
    Collections.sort(grades, new Comparator<Triplet<String, String, Double>>() {
      @Override
      public int compare(Triplet<String, String, Double> tup1, Triplet<String, String, Double> tup2) {
        return tup2.getValue2().compareTo(tup1.getValue2());
      }
    });

    grades.forEach((tup) -> System.out.println(tup));
  }

  // helper function
  public boolean hasProperty(String name, JsonArray arr) {
    Iterator<JsonElement> iterator = arr.iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getAsJsonObject().has(name)) {
        return true;
      }
    }
    return false;
  }

  public String getName() {
    return name;
  }

  public String toString() {

    return gradebook.toString();
  }

}
