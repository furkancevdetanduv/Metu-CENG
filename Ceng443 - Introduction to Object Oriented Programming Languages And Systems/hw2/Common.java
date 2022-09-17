package project.utility;

import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.logics.Builder;
import project.parts.logics.Fixer;
import project.parts.logics.Inspector;
import project.parts.logics.Supplier;
import project.parts.payloads.Camera;
import project.parts.payloads.Gripper;
import project.parts.payloads.MaintenanceKit;
import project.parts.payloads.Welder;

import java.util.Random;
import java.lang.reflect.Field;

public class Common
{

    public static Random random = new Random() ;

    public static synchronized Object get (Object object , String fieldName )
    {
        // TODO
        // This function retrieves (gets) the private field of an object by using reflection
        // In case the function needs to throw an exception, throw this: SmartFactoryException( "Failed: get!" )
        try{
            //get class of the object
            Class classObject = object.getClass();
            //get the field with fieldName
            Field field = classObject.getDeclaredField(fieldName);
            //allows the object to access the field
            field.setAccessible(true);
            // get the value of the field and return
            return field.get(object);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new project.utility.SmartFactoryException("Failed: get!");
        }
    }

    public static synchronized void set ( Object object , String fieldName , Object value )
    {
        // TODO
        // This function modifies (sets) the private field of an object by using reflection
        // In case the function needs to throw an exception, throw this: SmartFactoryException( "Failed: set!" )
        try{
            //get class of the object
            Class classObject = object.getClass();
            //get the field with fieldName
            Field field = classObject.getDeclaredField(fieldName);
            //allows the object to access the field
            field.setAccessible(true);
            // set the given value
            field.set(object, value);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SmartFactoryException("Failed: set!");
        }
    }
    // creates Base with serialNo
    public static synchronized Base factoryBase(int serialNo){
        return new Base(serialNo);
    }
    //creates Parts types with respect to partName
    public static synchronized Part factoryPart(String partName){
        Part part = null;
        switch (partName){
            case "Arm":
                part = new Arm();
                break;
            case "Fixer":
                part = new Fixer();
                break;
            case "Supplier":
                part = new Supplier();
                break;
            case "Inspector":
                part = new Inspector();
                break;
            case "Builder":
                part = new Builder();
                break;
            case "Welder":
                part = new Welder();
                break;
            case "MaintenanceKit":
                part = new MaintenanceKit();
                break;
            case "Gripper":
                part = new Gripper();
                break;
            case "Camera":
                part = new Camera();
                break;
        }

        return part;
    }
}