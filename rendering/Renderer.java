/**
 * Trieda Renderer zodpovedá za vykresľovanie vesmírnych telies (CelestialBody)
 * pomocou OpenGL. Spravuje shader program, nastavuje projekčnú a pohľadovú maticu
 * a vykonáva samotné vykresľovacie príkazy.
 *
 * @author Simon Kyselica
 */
package rendering;

import entities.CelestialBody;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import utils.Shader;

public class Renderer {
    private Shader shader; // Shader program používaný na vykresľovanie

    /**
     * Konštruktor pre triedu Renderer.
     * Inicializuje nový shader program a povolí testovanie hĺbky v OpenGL,
     * čo je nevyhnutné pre správne vykresľovanie 3D scény.
     */
    public Renderer() {
        this.shader = new Shader(); // Vytvorenie a kompilácia shader programu

        GL33.glEnable(GL33.GL_DEPTH_TEST);
    }

    /**
     * Inicializuje renderer nastavením projekčnej matice.
     * Projekčná matica definuje, ako sa 3D scéna premieta na 2D obrazovku.
     *
     * @param aspectRatio Pomer strán okna (šírka / výška), potrebný pre správne
     *                    nastavenie perspektívnej projekcie.
     */
    public void init(float aspectRatio) {
        Matrix4f projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f),  // Zorné pole (Field of View) v radiánoch
                aspectRatio,                    // Pomer strán
                0.1f,                           // Blízka orezávacia rovina (Near plane)
                100.0f                          // Vzdialená orezávacia rovina (Far plane)
        );

        this.shader.use(); // Aktivácia shader programu
        this.shader.setMat4("projection", projectionMatrix);
    }

    /**
     * Pripraví scénu na vykresľovanie nového snímku.
     * Vyčistí farebný buffer a hĺbkový buffer.
     * Táto metóda by sa mala volať na začiatku každého vykresľovacieho cyklu.
     */
    public void beginRender() {
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Vykreslí jedno vesmírne teleso (CelestialBody).
     * Aktivuje shader program, nastaví potrebné uniformné premenné (pohľadová matica,
     * modelová matica telesa) a vykoná príkaz na vykreslenie meshu telesa.
     *
     * @param body       Vesmírne teleso ({@link CelestialBody}), ktoré sa má vykresliť.
     * @param viewMatrix Pohľadová matica ({@link Matrix4f}) kamery.
     */
    public void render(CelestialBody body, Matrix4f viewMatrix) {
        this.shader.use(); // Aktivácia shader programu

        // Nastavenie uniformnej premennej 'view' (pohľadová matica) v shaderi
        this.shader.setMat4("view", viewMatrix);
        // Nastavenie uniformnej premennej 'model' (modelová matica) v shaderi
        this.shader.setMat4("model", body.getModelMatrix());

        // Bindovanie VAO meshu telesa
        GL33.glBindVertexArray(body.getMesh().getVaoId());
        // Vykreslenie prvkov (trojuholníkov) meshu
        GL33.glDrawElements(GL33.GL_TRIANGLES, body.getMesh().getVertexCount(), GL33.GL_UNSIGNED_INT, 0);
        // Unbindovanie VAO
        GL33.glBindVertexArray(0);
    }

    /**
     * Uvoľní zdroje alokované rendererom, konkrétne shader program.
     * Táto metóda by sa mala volať pri ukončení aplikácie, aby sa predišlo
     * únikom pamäte na strane GPU.
     */
    public void cleanup() {
        if (this.shader != null) {
            this.shader.cleanup(); // Uvoľnenie zdrojov shaderu
        }
    }
}
