package defense.common.explosive;

import java.lang.reflect.Method;

import defense.api.IExplosive;

public class ExplosiveHelper
{
    public static Class explosionManager;

    /** @return Gets an explosive object based on the name of the explosive. */
    public static IExplosive getExplosive(String name)
    {
        if (name != null)
        {
            try
            {
                Method method = explosionManager.getMethod("get", String.class);
                return (IExplosive) method.invoke(null, name);
            }
            catch (Exception e)
            {
                System.out.println("Failed to get explosive with the name: " + name);
                e.printStackTrace();
            }
        }

        return null;
    }
}
