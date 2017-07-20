package co.sugarware.colorflud;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Vector2Pool extends Pool<Vector2> {

    public Vector2 newObject(){
        return new Vector2();
    }

    public Vector2 obtain(float x, float y){
        Vector2 o = super.obtain();
        o.set(x, y);
        return o;
    }

    public Vector2 obtain(Vector2 v2){
        Vector2 o = super.obtain();
        o.set(v2);
        return o;
    }

    @Override
    protected void reset(Vector2 object) {
        object.set(0, 0);
    }
}
