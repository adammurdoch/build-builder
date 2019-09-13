package org.gradle.builds.model;

public class AndroidLibrary extends AndroidComponent implements JvmLibrary {
    private final String projectName;
    private JavaClassApi rClass;
    private JavaClass activity;

    public AndroidLibrary(Project project) {
        super(project.getQualifiedNamespaceFor());
        this.projectName = project.getName();
        rClass = JavaClassApi.field(getPackageName() + ".R.string", project.getName().toLowerCase() + "_string");
        activity = addClass(getPackageName() + "." + project.getTypeNameFor() + "Activity");
        activity.addRole(new AndroidActivity());
        activity(activity);
    }

    public JavaClass getActivity() {
        return activity;
    }

    @Override
    public AndroidLibraryApi getApi() {
        return new AndroidLibraryApi(projectName, activity.getApi(), rClass);
    }
}
