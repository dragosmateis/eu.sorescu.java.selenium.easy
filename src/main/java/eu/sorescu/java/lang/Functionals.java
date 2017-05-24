package eu.sorescu.java.lang;

public interface Functionals {
    interface SorescuRunnable{
        public abstract void run()throws Throwable;
    }
    interface SorescuSupplier<T>{
        public abstract T get()throws Throwable;
    }
    public static void TryRun(SorescuRunnable  runnable){
        try{runnable.run();}catch(Throwable t){
            if(t instanceof RuntimeException)throw (RuntimeException)t;
            throw new RuntimeException(t);
        }
    }
    public static <T>T TryGet(SorescuSupplier<T> runnable){
        try{return runnable.get();}catch(Throwable t){
            if(t instanceof RuntimeException)throw (RuntimeException)t;
            throw new RuntimeException(t);
        }
    }
}
