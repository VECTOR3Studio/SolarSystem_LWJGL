/**
 * Rozhranie CelestialBody definuje spoločné správanie pre všetky
 * vesmírne telesá v simulácii, ako sú planéty, mesiace alebo hviezdy.
 * Každé teleso musí implementovať metódy pre aktualizáciu svojho stavu,
 * poskytnutie svojej geometrie (mesh), pozície, modelovej matice a
 * pre uvoľnenie zdrojov.
 *
 * @author Simon Kyselica
 */
package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.Mesh;

public interface CelestialBody {
    /**
     * Aktualizuje stav vesmírneho telesa na základe uplynulého času.
     * Táto metóda sa typicky volá v každom snímku hernej slučky.
     * Môže zahŕňať výpočty orbitálneho pohybu, rotácie, atď.
     *
     * @param deltaTime Časový interval (v sekundách) od poslednej aktualizácie.
     */
    void update(float deltaTime);

    /**
     * Vráti geometrický model (mesh) vesmírneho telesa.
     * Mesh obsahuje vrcholy, indexy a ďalšie dáta potrebné pre vykreslenie.
     *
     * @return {@link Mesh} objekt reprezentujúci geometriu telesa.
     */
    Mesh getMesh();

    /**
     * Vráti aktuálnu pozíciu vesmírneho telesa v 3D priestore.
     *
     * @return {@link Vector3f} reprezentujúci pozíciu telesa.
     */
    Vector3f getPosition();

    /**
     * Nastaví novú pozíciu vesmírneho telesa v 3D priestore.
     *
     * @param position Nová pozícia telesa ako {@link Vector3f}.
     */
    void setPosition(Vector3f position);

    /**
     * Vráti modelovú maticu (model matrix) vesmírneho telesa.
     * Táto matica transformuje lokálne súradnice telesa do svetových súradníc
     * a zahŕňa jeho pozíciu, rotáciu a škálovanie.
     *
     * @return {@link Matrix4f} reprezentujúca modelovú maticu telesa.
     */
    Matrix4f getModelMatrix();

    /**
     * Uvoľní všetky zdroje alokované týmto vesmírnym telesom.
     * Táto metóda by sa mala volať, keď teleso už nie je potrebné,
     * aby sa predišlo únikom pamäte.
     */
    void cleanup();
}
