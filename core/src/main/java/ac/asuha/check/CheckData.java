package ac.asuha.check;

public @interface CheckData {
    
    String name() default "Unknown";
    
    String description() default "No description provided";
    
}
