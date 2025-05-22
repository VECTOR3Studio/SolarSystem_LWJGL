/**
 * Trieda Camera reprezentuje virtuálnu kameru v 3D priestore.
 * Umožňuje definovať pozíciu, orientáciu a pohyb kamery,
 * ako aj generovaťview matrix potrebnú pre vykresľovanie.
 *
 * @author Simon Kyselica
 */
package core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private Vector3f position; // Pozícia kamery v priestore
    private Vector3f front;    // Smer, ktorým sa kamera pozerá
    private Vector3f up;       // Vektor up pre kameru
    private float yaw;         // Otáčanie okolo vertikálnej osi (Y)
    private float pitch;       // Otáčanie okolo horizontálnej osi (X)
    private float speed;       // Rýchlosť pohybu kamery
    private float sensitivity; // Citlivosť myši pre otáčanie kamery
    private Matrix4f viewMatrix;

    /**
     * Konštruktor pre triedu Camera.
     * Inicializuje kameru s danou počiatočnou pozíciou a predvolenými hodnotami
     * pre smer, rýchlosť a citlivosť.
     *
     * @param position Počiatočná pozícia kamery ako {@link Vector3f}.
     */
    public Camera(Vector3f position) {
        this.position = position;
        this.front = new Vector3f(0, 0, -1); // Kamera sa na začiatku pozerá v smere zápornej osi Z
        this.up = new Vector3f(0, 1, 0);    // Vertikálna os je Y
        this.yaw = -90.0f; // Počiatočné natočenie, aby front smeroval k -Z
        this.pitch = 0.0f;
        this.speed = 0.08f; // Rýchlosť pohybu kamery
        this.sensitivity = 0.1f; // Citlivosť otáčania myšou
        this.viewMatrix = new Matrix4f();
        this.updateViewMatrix(); // Vypočíta počiatočnú view matrix
    }

    /**
     * Aktualizuje view matrix kamery na základe jej aktuálnej pozície,
     * smeru pohľadu (front) a vektora hore.
     * Používa sa metóda lookAt na vytvorenie matice.
     */
    private void updateViewMatrix() {
        // Cieľový bod, na ktorý sa kamera pozerá
        Vector3f center = new Vector3f(this.position).add(this.front);
        this.viewMatrix.identity().lookAt(this.position, center, this.up);
    }

    /**
     * Vráti aktuálnu pohľadovú maticu kamery.
     * Táto matica sa používa v renderovacom procese na transformáciu
     * súradníc sveta do súradníc kamery.
     *
     * @return Pohľadová matica (view matrix) ako {@link Matrix4f}.
     */
    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    /**
     * Spracováva vstup z klávesnice pre pohyb kamery.
     * Umožňuje pohyb dopredu (W), dozadu (S), doľava (A) a doprava (D).
     * Po každom pohybe aktualizuje pohľadovú maticu.
     *
     * @param window Identifikátor GLFW okna, pre ktoré sa zisťuje stav kláves.
     */
    public void processKeyboard(long window) {
        // Pohyb dopredu
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            this.position.add(new Vector3f(this.front).mul(this.speed));
        }
        // Pohyb dozadu
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            this.position.sub(new Vector3f(this.front).mul(this.speed));
        }
        // Pohyb doľava
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            Vector3f right = new Vector3f();
            this.front.cross(this.up, right).normalize();
            right.negate();
            this.position.add(new Vector3f(right).mul(this.speed));
        }
        // Pohyb doprava
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            Vector3f right = new Vector3f();
            this.front.cross(this.up, right).normalize();
            this.position.add(new Vector3f(right).mul(this.speed));
        }
        this.updateViewMatrix();
    }

    /**
     * Spracováva vstup z myši pre otáčanie kamery.
     * Aktualizuje uhly yaw a pitch na základe zmeny pozície kurzora myši.
     * Obmedzuje pitch, aby sa predišlo prevráteniu kamery.
     * Následne prepočíta vektor {@code front} a aktualizuje pohľadovú maticu.
     *
     * @param offsetX Zmena pozície myši v osi X.
     * @param offsetY Zmena pozície myši v osi Y.
     */
    public void processMouse(float offsetX, float offsetY) {
        this.yaw += offsetX * this.sensitivity;
        this.pitch -= offsetY * -this.sensitivity; // Inverzia offsetY pre intuitívne ovládanie

        // Obmedzenie uhla pitch, aby sa kamera neprevrátila
        this.pitch = Math.max(-89.0f, Math.min(89.0f, this.pitch));

        // Výpočet nového vektora front na základe yaw a pitch
        Vector3f newFront = new Vector3f();
        newFront.x = (float) Math.cos(Math.toRadians(this.yaw)) * (float) Math.cos(Math.toRadians(this.pitch));
        newFront.y = (float) Math.sin(Math.toRadians(this.pitch));
        newFront.z = (float) Math.sin(Math.toRadians(this.yaw)) * (float) Math.cos(Math.toRadians(this.pitch));

        this.front.set(newFront.normalize()); // Nastavenie a normalizácia nového smeru pohľadu
        this.updateViewMatrix();
    }
}
