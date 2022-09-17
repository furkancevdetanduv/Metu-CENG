import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.ConcurrentHashMap;

public class SIS {
    private static String fileSep = File.separator;
    private static String lineSep = System.lineSeparator();
    private static String space   = " ";

    private List<Student> studentList = new ArrayList<>();

    public SIS(){ processOptics(); }

    private void processOptics(){
        // TODO
        try{
            //get current directory
            File currentDirectory = new File(System.getProperty("user.dir"));
            //read input file
            Stream<Path> paths = Files.list(Paths.get(currentDirectory.getParent() + fileSep + "input"));
            //List<File> inputFiles = Files.walk(Paths.get("input")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
            Stream<String> lines = paths.flatMap(e -> {
                try {
                    return Files.lines(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            AtomicInteger counter = new AtomicInteger();
            Collection<List<String>> linesEachFile = lines
                    .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 4))
                    .values();
            //System.out.println(linesEachFile);
            //process lines
            linesEachFile.stream().forEach(e -> {
                //get student info
                String[] studentInfo = e.get(0).split(space,10);//increase limit
                //get course info
                String[] courseInfo = e.get(1).split(space,3);
                List<String> names = new ArrayList<String>();
                int studentNo;
                names.add(studentInfo[0]);
                if (studentInfo.length == 3){
                    studentNo = Integer.parseInt(studentInfo[2]);
                }
                else{
                    names.add(studentInfo[1]);//there can be more than 2 names
                    studentNo = Integer.parseInt(studentInfo[studentInfo.length-1]);
                }
                Optional<Student> student = studentList.stream().filter(s -> s.getStudentID() == studentNo).findAny();
                //int grade = StringUtils.countMatches(e.get(3), "T") * (100/e.get(3).length());
                double grade = (int )e.get(3).chars().filter(ch -> ch == 'T').count() * (100.0/e.get(3).length());
                //System.out.println((int )e.get(3).chars().filter(ch -> ch == 'T').count());
                //System.out.println((100.0/(e.get(3).length())));
                //System.out.println(grade);
                Course course = new Course(Integer.parseInt(courseInfo[1]),Integer.parseInt(courseInfo[0]),e.get(2),Integer.parseInt(courseInfo[2]),grade); //courseCode, year , examtype , credit, grade
                //System.out.println(course.getExamType());
                //if student exists add course to his course list
                if(student.isPresent()){
                    //add course to student
                    //student.get().addCourse(course);
                    student.get().getTakenCourses().add(course);
                }
                else{ // create new student
                    Student newStudent = new Student( names.toArray(new String[0]), studentInfo[1],studentNo) ;
                    studentList.add(newStudent);
                    //add course to student
                    //newStudent.addCourse(course);
                    newStudent.getTakenCourses().add(course);
                }
                //check if student exists
            });
            //System.out.println(studentList);
        }
        catch (IOException e){
            return;
        }

    }

    public double getGrade(int studentID, int courseCode, int year){
        // TODO
        Optional<Student> student = studentList.stream().filter(s -> s.getStudentID() == studentID).findAny();
        //check if student exits
        if(student.isPresent()){
            //get exams
            Optional<Course> midterm1 = student.get().getTakenCourses().stream().filter(c -> c.getYear() == year && c.getCourseCode() == courseCode && c.getExamType().equals("Midterm1") ).findAny();
            Optional<Course> midterm2 = student.get().getTakenCourses().stream().filter(c -> c.getYear() == year && c.getCourseCode() == courseCode && c.getExamType().equals("Midterm2") ).findAny();
            Optional<Course> finalExam = student.get().getTakenCourses().stream().filter(c -> c.getYear() == year && c.getCourseCode() == courseCode && c.getExamType().equals("Final") ).findAny();

            double mt1Grade = 0;
            double mt2Grade = 0;
            double finalGrade = 0 ;

            //get exam grades if they exist
            if(midterm1.isPresent()){
                mt1Grade = midterm1.get().getGrade();
            }

            if(midterm2.isPresent()){
                mt2Grade = midterm2.get().getGrade();
            }

            if(finalExam.isPresent()){
                finalGrade = finalExam.get().getGrade();
            }

            //course not taken return -1
            if(mt1Grade == 0 && mt2Grade==0 && finalGrade == 0){
                return -1;
            }
            return ((0.25*mt1Grade) +(0.25*mt2Grade) + (0.50*finalGrade));
        }
        return -1;
    }

    public void updateExam(int studentID, int courseCode, String examType, double newGrade){
        // TODO
        Optional<Student> student = studentList.stream().filter(s -> s.getStudentID() == studentID).findAny();
        if(student.isPresent()){
            //get taken courses
            Stream<Course> takenCourses = student.get().getTakenCourses().stream();
            List<Course> updateCourses = takenCourses.filter(c -> c.getCourseCode() == courseCode && c.getExamType().equals(examType)).collect(Collectors.toList());
            //find course
            Optional<Course> recentCourse = updateCourses.stream().max(Comparator.comparing(Course::getYear));
            if (recentCourse.isPresent()){
                recentCourse.get().setGrade(newGrade); // set new grade
            }

        }
    }

    public void createTranscript(int studentID){
        // TODO
        //find student
        Optional<Student> student = studentList.stream().filter(s -> s.getStudentID() == studentID).findAny();
        if(student.isPresent()){ // student exists
            // Create a map year to course list
            Map<Integer, List<Course>> courseYears = student.get().getTakenCourses().stream().collect(Collectors.groupingBy(Course::getYear));
            // sort map with treemap
            TreeMap<Integer, List<Course>> courseYearsSorted = new TreeMap<Integer, List<Course>>(courseYears);
            // these two lists will use for sum gpa and sum credit
            List<Double> gpaList = new ArrayList<Double>();
            List<Integer> creditList = new ArrayList<Integer>();
            //List<Double> gpaThing = new ArrayList<Double>();
            int totalCredit = 0 ;
            courseYearsSorted.forEach((k,v) -> {
                //Collections.sort(v, Comparator.comparingInt(Course::getCourseCode));
                // group by course code
                Map<Integer,List<Course>> courseCode = v.stream().collect(Collectors.groupingBy(Course::getCourseCode));
                // sort by course code
                TreeMap<Integer, List<Course>> courseCodeSorted = new TreeMap<Integer, List<Course>>(courseCode);
                if(!courseCode.isEmpty()){
                    System.out.println(k);
                    courseCodeSorted.forEach((key,value) ->{
                        double grade = Math.round(getGrade(studentID, key,k));
                        int courseCredit = value.get(0).getCredit();
                        creditList.add(courseCredit);
                        String letterGrade;
                        // obtain letter grade
                        if(grade >= 90.0){
                            letterGrade = "AA";
                            gpaList.add(4.0 * courseCredit);
                        }
                        else if(grade >= 85.0){
                            letterGrade = "BA";
                            gpaList.add(3.5 * courseCredit);
                        }
                        else if(grade >= 80.0){
                            letterGrade = "BB";
                            gpaList.add(3.0 * courseCredit);
                        }
                        else if(grade >= 75.0){
                            letterGrade = "CB";
                            gpaList.add(2.5 * courseCredit);
                        }
                        else if(grade >= 70.0){
                            letterGrade = "CC";
                            gpaList.add(2.0 * courseCredit);
                        }
                        else if(grade >= 65.0){
                            letterGrade = "DC";
                            gpaList.add(1.5 * courseCredit);
                        }
                        else if(grade >= 60.0){
                            letterGrade = "DD";
                            gpaList.add(1.0 * courseCredit);
                        }
                        else if(grade >= 50.0){
                            letterGrade = "FD";
                            gpaList.add(0.5 * courseCredit);
                        }
                        else if(grade >= 0){
                            letterGrade = "FF";
                            //gpaList.add(0.0 );
                        }
                        else {
                            letterGrade = "NA";
                            gpaList.add(0.0 );
                        }
                        //print letter grade
                        System.out.println(key.toString() + " " + letterGrade );
                    } );
                }
            });
            // calculate cgpa
            double cgpa = gpaList.stream().mapToDouble(a -> a.doubleValue()).sum() /
                    creditList.stream().mapToInt(a -> a.intValue()).sum() ;
            //double cgpa = gpaThing.stream().mapToDouble(a -> a.doubleValue()).sum() / gpaThing.size() ;
            //cgpa = Math.round(cgpa,2);
            DecimalFormat df = new DecimalFormat("####0.00");
            System.out.println("CGPA: " + df.format(cgpa));
            //System.out.println("CGPA: %.2f ", cgpa);
        }
    }

    public void findCourse(int courseCode){
        // TODO
        List<Student> registeredStudentList = new ArrayList<Student>();
        TreeMap<Integer, Integer> studentNumbers = new TreeMap<Integer, Integer>();
        studentList.forEach(s -> {
            List<Course> takenCourse = s.getTakenCourses();
            List<Integer> courseYears = takenCourse.stream().filter(c -> c.getCourseCode() == courseCode).map(Course::getYear).distinct().collect(Collectors.toList());
            if (courseYears.size() != 0){
                courseYears.forEach(y -> {
                    if( studentNumbers.containsKey(y)){// key exists increment value by 1
                        studentNumbers.put(y,studentNumbers.get(y)+1);
                    }
                    else {//create new key with value 1
                        studentNumbers.put(y,1);
                    }
                });
            }
        });
        //print result
        studentNumbers.forEach((k,v) -> {
            System.out.println(String.valueOf(k) + " " +String.valueOf(v));
        });
    }

    public void createHistogram(int courseCode, int year){
        // TODO
        // Create a map for grade ranges
        HashMap<Integer,Integer> histogramMap = new HashMap<Integer, Integer>();
        histogramMap.put(0,0);
        histogramMap.put(10,0);
        histogramMap.put(20,0);
        histogramMap.put(30,0);
        histogramMap.put(40,0);
        histogramMap.put(50,0);
        histogramMap.put(60,0);
        histogramMap.put(70,0);
        histogramMap.put(80,0);
        histogramMap.put(90,0);
        studentList.forEach(s -> {
            double grade = getGrade(s.getStudentID(),courseCode,year);
            if (grade != -1){// if grade not -1 put increment value for corresponding range for key
                if (grade >= 90.0){
                    histogramMap.put(90,histogramMap.get(90)+1);
                }
                else if (grade >= 80.0){
                    histogramMap.put(80,histogramMap.get(80)+1);
                }
                else if (grade >= 70.0){
                    histogramMap.put(70,histogramMap.get(70)+1);
                }
                else if (grade >= 60.0){
                    histogramMap.put(60,histogramMap.get(60)+1);
                }
                else if (grade >= 50.0){
                    histogramMap.put(50,histogramMap.get(50)+1);
                }
                else if (grade >= 40.0){
                    histogramMap.put(40,histogramMap.get(40)+1);
                }
                else if (grade >= 30.0){
                    histogramMap.put(30,histogramMap.get(30)+1);
                }
                else if (grade >= 20.0){
                    histogramMap.put(20,histogramMap.get(20)+1);
                }
                else if (grade >= 10.0){
                    histogramMap.put(10,histogramMap.get(10)+1);
                }
                else if(grade >= 0.0){
                    histogramMap.put(0,histogramMap.get(0)+1);
                }
            }
        });//print histogram
        System.out.println("0-10 " + String.valueOf(histogramMap.get(0)));
        System.out.println("10-20 " + String.valueOf(histogramMap.get(10)));
        System.out.println("20-30 "+ String.valueOf(histogramMap.get(20)));
        System.out.println("30-40 "+ String.valueOf(histogramMap.get(30)));
        System.out.println("40-50 "+ String.valueOf(histogramMap.get(40)));
        System.out.println("50-60 "+ String.valueOf(histogramMap.get(50)));
        System.out.println("60-70 "+ String.valueOf(histogramMap.get(60)));
        System.out.println("70-80 "+ String.valueOf(histogramMap.get(70)));
        System.out.println("80-90 "+ String.valueOf(histogramMap.get(80)));
        System.out.println("90-100 "+ String.valueOf(histogramMap.get(90)));
    }
}