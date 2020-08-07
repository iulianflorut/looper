package data.looper;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class App {

	public static void main(String[] args) {

		Supplier<Filter> filter = Filter::new;

		new Reflections(App.class.getPackageName(), new SubTypesScanner(false)).getSubTypesOf(Object.class).stream().forEach(c -> {
			Stream.of(c.getDeclaredMethods()).filter(m -> m.getAnnotation(Execute.class) != null && m.getAnnotation(Execute.class).value()).findFirst()
					.ifPresent(m -> {
						try {
							System.out.println("started " + c.getSimpleName() + "." + m.getName());
							m.setAccessible(true);
							m.invoke(null, filter.get());
							System.out.println("finished " + c.getSimpleName() + "." + m.getName());
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					});
		});
	}

}
