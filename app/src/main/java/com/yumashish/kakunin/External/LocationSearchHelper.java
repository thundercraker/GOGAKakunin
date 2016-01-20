package com.yumashish.kakunin.External;

import android.location.Location;

import com.android.internal.util.Predicate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Created by lightning on 1/12/16.
 */
public class LocationSearchHelper {
    List<LSObject> locationObjects;
    LSObject defaultInstance;

    public LocationSearchHelper(LSObject defaultInstance, InputStream csvIS) {
        this.defaultInstance = defaultInstance;
        locationObjects = new ArrayList<LSObject>();

        try {
            parseCSV(csvIS);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void parseCSV(InputStream is) throws IOException {
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(new InputStreamReader(is), CsvPreference.STANDARD_PREFERENCE);
            final String[] header = beanReader.getHeader(true);

            LSObject itemBean;
            while((itemBean = beanReader.read(defaultInstance.getClass(), header, defaultInstance.getCellProcessors())) != null) {
                locationObjects.add(itemBean);
            }
        } finally {
            if(beanReader != null) {
                beanReader.close();
            }
        }
    }

    public LocationSearchHelper(LocationSearchHelper copy) {
        this.defaultInstance = copy.defaultInstance;
        this.locationObjects = new ArrayList<>(copy.locationObjects);
    }

    public void setLSOObjects(List<LSObject> objects) { locationObjects = objects; }
    public List<LSObject> getLSObjects() {
        return locationObjects;
    }

    public List<LSObject> Search(List<Predicate> predicates) {
        List<LSObject> results = new ArrayList<LSObject>();
        for (Object object : locationObjects) {
            boolean testPass = true;
            for (Predicate predicate : predicates) {
                if(!predicate.apply(object)) {
                    testPass = false;
                    break;
                }
            }
            //System.out.println("Predicate " + object.getName() + " " +testPass);
            if(testPass) results.add((LSObject) object);
        }
        return results;
    }

    public static class PredicateBuilder {
        private List<Predicate> predicates;

        public PredicateBuilder() {
            predicates = new ArrayList();
        }

        public PredicateBuilder(PredicateBuilder predicateBuilder) {
            predicates = new ArrayList<Predicate>();
            predicates.addAll(predicateBuilder.predicates);
        }

        public PredicateBuilder AddWithinBounds(final double Ax, final double Ay, final double Bx, final double By) {
            this.predicates.add(new Predicate<LSObject>() {
                public boolean apply(LSObject o) {
                    if (o.latitude < Math.max(Ax, Bx) && o.latitude > Math.min(Ax, Bx)
                            && o.longitude < Math.max(Ay, By) && o.longitude > Math.min(Ay, By)) {
                        return true;
                    }
                    return false;
                }
            });
            return this;
        }

        public PredicateBuilder AddWithinRadius(final double lat, final double lng, final double radius) {
            this.predicates.add(new Predicate<LSObject>() {
                @Override
                public boolean apply(LSObject o) {
                    float[] results = new float[1];
                    Location.distanceBetween(lat, lng, o.latitude, o.longitude, results);
                    return (radius >= results[0]);
                }
            });
            return this;
        }

        public PredicateBuilder AddCategoryTrue(final String categoryName) {
            this.predicates.add(new Predicate() {
                public boolean apply(Object locationObject) {
                    try {
                        Field field = locationObject.getClass().getDeclaredField(categoryName);
                        field.setAccessible(true);
                        if (field.getType() == int.class) {
                            int val = field.getInt(locationObject);
                            if(val == 1) return true;
                        } else if (field.getType() == boolean.class) {
                            boolean val = field.getBoolean(locationObject);
                            return val;
                        } else {
                            //System.out.println("Cannot resolve " + field.getType() + " to boolean");
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return false;
                }
            });
            return this;
        }

        public PredicateBuilder AddElementEquals(final String categoryName, final Object categoryValue) {
            this.predicates.add(new Predicate() {
                public boolean apply(Object locationObject) {
                    try {
                        Field field = locationObject.getClass().getDeclaredField(categoryName);
                        field.setAccessible(true);
                        Object val = field.get(locationObject);
                        if(categoryValue.equals(val)) return true;
                        else return false;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
            return this;
        }

        public PredicateBuilder AddPredicate(Predicate predicate) {
            this.predicates.add(predicate);
            return this;
        }

        public List<Predicate> Build() {
            return new ArrayList<Predicate>(predicates);
        }
    }
}

