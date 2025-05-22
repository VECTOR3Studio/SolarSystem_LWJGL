/**
 * Trieda Sphere reprezentuje základný guľový objekt v 3D priestore.
 * Implementuje rozhranie {@link CelestialBody}, čo znamená, že môže byť
 * použitá ako základ pre rôzne vesmírne telesá (planéty, mesiace, hviezdy).
 * Zodpovedá za vytvorenie svojej geometrie (mesh), uchovávanie pozície,
 * rotácie, škály, farby a za vlastnú rotáciu okolo osi Y.
 *
 * @author Simon Kyselica
 */
package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.Mesh;

public class Sphere implements CelestialBody {
    private Mesh mesh;
    private Vector3f position;
    private float radius;
    private Vector3f rotation; // Rotácia v stupňoch okolo osí X, Y, Z
    private float scale;
    private Vector3f baseColor; // Základná farba gule
    private float rotationPeriod; // Perióda rotácie okolo vlastnej osi Y (v sekundách)
    private boolean enableSelfRotation; // Či je povolená vlastná rotácia

    /**
     * Konštruktor pre triedu Sphere.
     * Inicializuje guľu s danou pozíciou, polomerom, základnou farbou a periódou rotácie.
     * Automaticky vytvára geometriu (mesh) gule.
     *
     * @param position       Počiatočná pozícia stredu gule ako {@link Vector3f}.
     * @param radius         Polomer gule.
     * @param baseColor      Základná farba gule ako {@link Vector3f}. Ak je {@code null}, použije sa predvolená šedá farba.
     * @param rotationPeriod Doba (napr. v sekundách), za ktorú sa guľa otočí o 360 stupňov okolo svojej osi Y.
     *                       Ak je 0, vlastná rotácia bude vypnutá.
     */
    public Sphere(
            Vector3f position,
            float radius,
            Vector3f baseColor,
            float rotationPeriod
    ) {
        this.position = position;
        this.radius = radius;
        this.rotation = new Vector3f(0, 0, 0); // Počiatočná rotácia
        this.scale = 1.0f; // Počiatočná velkost
        this.baseColor = baseColor != null
                ? baseColor
                : new Vector3f(0.5f, 0.5f, 0.5f); // Predvolená farba, ak nie je zadaná
        this.rotationPeriod = rotationPeriod;
        this.enableSelfRotation = (rotationPeriod != 0.0f); // Povolenie rotácie, ak perióda nie je 0
        this.createMesh(); // Vytvorenie geometrie gule
    }


    private void createMesh() {
        int stacks = 24; // Počet horizontálnych pásov
        int sectors = 48; // Počet vertikálnych segmentov

        int numVertices = (stacks + 1) * (sectors + 1);
        int numIndices = stacks * sectors * 6;
        float[] vertices = new float[numVertices * 6]; // 3 pre pozíciu, 3 pre farbu
        int[] indices = new int[numIndices];

        float stackStep = (float) Math.PI / stacks;
        float sectorStep = 2.0f * (float) Math.PI / sectors;

        int vertexIndex = 0;
        for (int i = 0; i <= stacks; i++) {
            float stackAngle = (float) Math.PI / 2 - i * stackStep; // Od PI/2 po -PI/2
            float xy = this.radius * (float) Math.cos(stackAngle); // Polomer kružnice pre daný stack
            float zPos = this.radius * (float) Math.sin(stackAngle); // Z-ová súradnica

            for (int j = 0; j <= sectors; j++) {
                float sectorAngle = j * sectorStep; // Od 0 po 2*PI

                // Súradnice vrcholu
                float xPos = xy * (float) Math.cos(sectorAngle);
                float yPos = xy * (float) Math.sin(sectorAngle);

                vertices[vertexIndex++] = xPos;
                vertices[vertexIndex++] = yPos;
                vertices[vertexIndex++] = zPos;

                // Jednoduché farebné variácie založené na pozícii pre vizuálny efekt
                float colorR =
                        this.baseColor.x *
                                (0.8f + 0.2f * ((float) Math.cos(stackAngle) + 1.0f) / 2.0f);
                float colorG =
                        this.baseColor.y *
                                (0.8f + 0.2f * ((float) Math.cos(sectorAngle) + 1.0f) / 2.0f);
                float colorB =
                        this.baseColor.z *
                                (
                                        0.8f +
                                                0.2f *
                                                        ((float) Math.sin(stackAngle * 2) + 1.0f) /
                                                        2.0f
                                );

                vertices[vertexIndex++] = Math.max(0, Math.min(1, colorR));
                vertices[vertexIndex++] = Math.max(0, Math.min(1, colorG));
                vertices[vertexIndex++] = Math.max(0, Math.min(1, colorB));
            }
        }

        // Vytvorenie indexov pre trojuholníky
        int indexIndex = 0;
        for (int i = 0; i < stacks; i++) {
            int k1 = i * (sectors + 1); // Začiatok aktuálneho stacku
            int k2 = k1 + sectors + 1;  // Začiatok nasledujúceho stacku

            for (int j = 0; j < sectors; j++, k1++, k2++) {
                // 2 trojuholníky na sektor (okrem pólov)
                if (i != 0) { // Horný trojuholník
                    indices[indexIndex++] = k1;
                    indices[indexIndex++] = k2;
                    indices[indexIndex++] = k1 + 1;
                }

                if (i != (stacks - 1)) { // Dolný trojuholník (upravené pre správne fungovanie)
                    indices[indexIndex++] = k1 + 1;
                    indices[indexIndex++] = k2;
                    indices[indexIndex++] = k2 + 1;
                }
            }
        }
        this.mesh = new Mesh(vertices, indices);
    }

    /**
     * Vráti geometrický model (mesh) gule.
     *
     * @return {@link Mesh} objekt reprezentujúci geometriu gule.
     */
    @Override
    public Mesh getMesh() {
        return this.mesh;
    }

    /**
     * Vráti aktuálnu pozíciu stredu gule v 3D priestore.
     *
     * @return {@link Vector3f} reprezentujúci pozíciu gule.
     */
    @Override
    public Vector3f getPosition() {
        return this.position;
    }

    /**
     * Nastaví novú pozíciu stredu gule v 3D priestore.
     *
     * @param position Nová pozícia gule ako {@link Vector3f}.
     */
    @Override
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Vráti modelovú maticu (model matrix) gule.
     * Táto matica transformuje lokálne súradnice gule do svetových súradníc
     * a zahŕňa jej pozíciu, rotáciu a škálovanie.
     *
     * @return {@link Matrix4f} reprezentujúca modelovú maticu gule.
     */
    @Override
    public Matrix4f getModelMatrix() {
        Matrix4f modelMatrix = new Matrix4f()
                .identity()
                .translate(this.position)
                .rotateX((float) Math.toRadians(this.rotation.x))
                .rotateY((float) Math.toRadians(this.rotation.y))
                .rotateZ((float) Math.toRadians(this.rotation.z))
                .scale(this.scale);
        return modelMatrix;
    }

    /**
     * Aplikuje rotáciu na guľu okolo jej osí X, Y a Z.
     * Hodnoty rotácie sú prírastkové a udávajú sa v stupňoch.
     *
     * @param dx Prírastok rotácie okolo osi X v stupňoch.
     * @param dy Prírastok rotácie okolo osi Y v stupňoch.
     * @param dz Prírastok rotácie okolo osi Z v stupňoch.
     */
    public void rotate(float dx, float dy, float dz) {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }

    /**
     * Aktualizuje stav gule na základe uplynulého času.
     * Ak je povolená vlastná rotácia ({@code enableSelfRotation} je {@code true}
     * a {@code rotationPeriod} nie je 0), guľa sa otočí okolo svojej osi Y
     * na základe definovanej periódy rotácie.
     *
     * @param deltaTime Časový interval (v sekundách) od poslednej aktualizácie.
     */
    @Override
    public void update(float deltaTime) {
        if (this.enableSelfRotation && this.rotationPeriod != 0.0f) {
            // Výpočet rýchlosti rotácie v stupňoch za sekundu
            float rotationSpeed = 360.0f / this.rotationPeriod;
            // Aplikácia rotácie okolo osi Y
            this.rotate(0, rotationSpeed * deltaTime, 0);
        }
    }

    /**
     * Uvoľní všetky zdroje alokované touto guľou, konkrétne jej mesh.
     * Táto metóda by sa mala volať, keď guľa už nie je potrebná.
     */
    @Override
    public void cleanup() {
        if (this.mesh != null) {
            this.mesh.cleanup();
        }
    }
}
