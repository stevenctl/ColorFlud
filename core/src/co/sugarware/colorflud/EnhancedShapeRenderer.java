package co.sugarware.colorflud;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by slandow on 7/20/17.
 */

public class EnhancedShapeRenderer extends ShapeRenderer {

    public void roundedRect(float x, float y, float width, float height, float radius) {
        // Central rectangle
        super.rect(x + radius, y + radius, width - 2 * radius, height - 2 * radius);

        // Four side rectangles, in clockwise order
        super.rect(x + radius, y, width - 2 * radius, radius);
        super.rect(x + width - radius, y + radius, radius, height - 2 * radius);
        super.rect(x + radius, y + height - radius, width - 2 * radius, radius);
        super.rect(x, y + radius, radius, height - 2 * radius);

        // Four arches, clockwise too
        super.arc(x + radius, y + radius, radius, 180f, 90f);
        super.arc(x + width - radius, y + radius, radius, 270f, 90f);
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        super.arc(x + radius, y + height - radius, radius, 90f, 90f);
    }

}
