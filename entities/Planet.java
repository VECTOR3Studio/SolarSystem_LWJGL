/**
 * Trieda Planet reprezentuje planétu v slnečnej sústave.
 * Rozširuje triedu {@link Sphere} a pridáva logiku pre orbitálny pohyb
 * okolo iného centrálneho telesa (typicky hviezdy) alebo môže mať fixnú pozíciu,
 * ak neobieha okolo ničoho.
 * Planéta má definovaný polomer, farbu, periódu rotácie, ako aj orbitálny
 * polomer a periódu obehu.
 *
 * @author Simon Kyselica
 */
package entities;

import org.joml.Vector3f;

public class Planet extends Sphere {
    private float orbitalRadius;
    private float orbitalPeriod;
    private float orbitalPositionAngle;
    private CelestialBody orbitsAround;
    private Vector3f relativePositionToParent;

    /**
     * Konštruktor pre triedu Planet, ktorá obieha okolo iného telesa.
     * Inicializuje planétu s jej fyzikálnymi vlastnosťami, orbitálnymi charakteristikami
     * a telesom, okolo ktorého obieha.
     *
     * @param initialPosition      Počiatočná pozícia planéty. Ak {@code orbitsAround} nie je {@code null},
     *                             táto pozícia sa bude meniť na základe orbitálneho pohybu.
     * @param radius               Polomer planéty.
     * @param orbitalRadiusParam   Základný polomer obežnej dráhy planéty. K tejto hodnote sa pripočíta konštanta (2f).
     * @param orbitalPeriod        Doba (napr. v sekundách), za ktorú planéta dokončí jeden obeh.
     *                             Ak je 0, planéta nebude aktívne orbitovať.
     * @param rotationPeriod       Doba (napr. v sekundách), za ktorú sa planéta otočí okolo vlastnej osi.
     * @param color                Farba planéty ako {@link Vector3f}.
     * @param orbitsAround         {@link CelestialBody}, okolo ktorého táto planéta obieha (napr. hviezda).
     *                             Môže byť {@code null}, v takom prípade planéta neobieha.
     */
    public Planet(
            Vector3f initialPosition,
            float radius,
            float orbitalRadiusParam,
            float orbitalPeriod,
            float rotationPeriod,
            Vector3f color,
            CelestialBody orbitsAround
    ) {
        super(initialPosition, radius, color, rotationPeriod);
        this.orbitalRadius = orbitalRadiusParam + 2f;
        this.orbitalPeriod = orbitalPeriod;
        this.orbitsAround = orbitsAround;
        this.orbitalPositionAngle = 0.0f;
        this.relativePositionToParent = new Vector3f();
    }

    /**
     * Preťažený konštruktor pre triedu Planet, ktorá neobieha okolo žiadneho iného telesa (napr. hviezda).
     * Inicializuje planétu (alebo hviezdu) s jej fyzikálnymi vlastnosťami.
     * Parameter {@code orbitsAround} je interne nastavený na {@code null}.
     *
     * @param initialPosition      Počiatočná (a zvyčajne fixná) pozícia telesa.
     * @param radius               Polomer telesa.
     * @param orbitalRadiusParam   Základný polomer obežnej dráhy (v tomto kontexte menej relevantný, ale zachovaný pre konzistenciu).
     * @param orbitalPeriod        Doba obehu (nastavená, ale planéta nebude orbitovať, ak {@code orbitsAround} je {@code null}).
     * @param rotationPeriod       Doba (napr. v sekundách), za ktorú sa teleso otočí okolo vlastnej osi.
     * @param color                Farba telesa ako {@link Vector3f}.
     */
    public Planet(
            Vector3f initialPosition,
            float radius,
            float orbitalRadiusParam,
            float orbitalPeriod,
            float rotationPeriod,
            Vector3f color
    ) {
        this(
                initialPosition,
                radius,
                orbitalRadiusParam,
                orbitalPeriod,
                rotationPeriod,
                color,
                null
        );
    }

    /**
     * Aktualizuje stav planéty na základe uplynulého času.
     * Táto metóda najprv volá {@code update} metódu nadtriedy {@link Sphere}
     * na spracovanie rotácie. Následne, ak má planéta definovanú obežnú periódu
     * a obieha okolo iného telesa, vypočíta jej novú pozíciu na obežnej dráhe.
     * Ak planéta neobieha ({@code orbitsAround} je {@code null} alebo {@code orbitalPeriod} je 0),
     * jej pozícia sa nemení orbitálnym pohybom, ale stále sa môže meniť rotácia.
     *
     * @param deltaTime Časový interval (v sekundách) od poslednej aktualizácie.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (this.orbitalPeriod != 0) {
            float orbitalSpeed = 2.0f * (float) Math.PI / this.orbitalPeriod;
            this.orbitalPositionAngle += orbitalSpeed * deltaTime;
            if (this.orbitalPositionAngle >= 2 * Math.PI) {
                this.orbitalPositionAngle -= 2 * Math.PI;
            }

            this.relativePositionToParent.x =
                    this.orbitalRadius * (float) Math.cos(this.orbitalPositionAngle);
            this.relativePositionToParent.y = 0;
            this.relativePositionToParent.z =
                    this.orbitalRadius * (float) Math.sin(this.orbitalPositionAngle);
        } else {
            this.relativePositionToParent.set(0, 0, 0);
        }

        Vector3f parentPosition = new Vector3f(0, 0, 0);
        if (this.orbitsAround != null) {
            parentPosition = this.orbitsAround.getPosition();
        }

        this.setPosition(new Vector3f(parentPosition).add(this.relativePositionToParent));
    }

}
