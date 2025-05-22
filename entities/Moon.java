/**
 * Trieda Moon reprezentuje mesiac, ktorý obieha okolo iného vesmírneho telesa (planéty).
 * Rozširuje triedu {@link Sphere} a pridáva logiku pre orbitálny pohyb.
 * Mesiac má definovaný polomer, farbu, periódu rotácie, ako aj orbitálny
 * polomer a periódu obehu okolo svojho materského telesa.
 *
 * @author Simon Kyselica
 */

package entities;

import org.joml.Vector3f;

public class Moon extends Sphere {
    private float orbitalRadius;
    private float orbitalPeriod;
    private float orbitalPositionAngle;
    private CelestialBody orbitsAround;

    /**
     * Konštruktor pre triedu Moon.
     * Inicializuje mesiac s jeho fyzikálnymi vlastnosťami (polomer, farba, perióda rotácie)
     * a orbitálnymi charakteristikami (orbitálny polomer, orbitálna perióda)
     * a telesom, okolo ktorého obieha. Počiatočná pozícia mesiaca je relatívna
     * k telesu, okolo ktorého obieha, a je aktualizovaná v metóde {@code update}.
     *
     * @param radius           Polomer mesiaca.
     * @param orbitalRadius    Polomer obežnej dráhy mesiaca okolo materského telesa.
     * @param orbitalPeriod    Doba (napr. v sekundách), za ktorú mesiac dokončí jeden obeh okolo materského telesa.
     *                         Ak je 0, mesiac nebude aktívne orbitovať, ale zostane pri materskom telese.
     * @param rotationPeriod   Doba (napr. v sekundách), za ktorú sa mesiac otočí okolo vlastnej osi.
     * @param color            Farba mesiaca ako {@link Vector3f}.
     * @param orbitsAround     {@link CelestialBody}, okolo ktorého tento mesiac obieha (typicky planéta).
     *                         Nesmie byť {@code null}.
     * @throws IllegalArgumentException ak {@code orbitsAround} je {@code null}.
     */
    public Moon(
            float radius,
            float orbitalRadius,
            float orbitalPeriod,
            float rotationPeriod,
            Vector3f color,
            CelestialBody orbitsAround
    ) {
        super(new Vector3f(0, 0, 0), radius, color, rotationPeriod);
        this.orbitalRadius = orbitalRadius;
        this.orbitalPeriod = orbitalPeriod;
        this.orbitsAround = orbitsAround;
        this.orbitalPositionAngle = 0.0f;

        if (orbitsAround == null) {
            throw new IllegalArgumentException(
                    "Moon must orbit a CelestialBody (Planet)."
            );
        }
    }

    /**
     * Aktualizuje stav mesiaca na základe uplynulého času.
     * Táto metóda najprv volá {@code update} metódu nadtriedy {@link Sphere}
     * na spracovanie rotácie. Následne vypočíta novú pozíciu mesiaca
     * na jeho obežnej dráhe okolo materského telesa {@code orbitsAround}.
     * Ak je {@code orbitalPeriod} nula, mesiac zostane na pozícii svojho materského telesa.
     *
     * @param deltaTime Časový interval (v sekundách) od poslednej aktualizácie.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (this.orbitalPeriod != 0 && this.orbitsAround != null) {
            float orbitalSpeed = 2.0f * (float) Math.PI / this.orbitalPeriod;
            this.orbitalPositionAngle += orbitalSpeed * deltaTime;
            if (this.orbitalPositionAngle >= 2 * Math.PI) {
                this.orbitalPositionAngle -= 2 * Math.PI;
            }

            Vector3f relativePositionToParent = new Vector3f();
            relativePositionToParent.x =
                    this.orbitalRadius * (float) Math.cos(this.orbitalPositionAngle);
            relativePositionToParent.y = 0;
            relativePositionToParent.z =
                    this.orbitalRadius * (float) Math.sin(this.orbitalPositionAngle);

            Vector3f parentPosition = this.orbitsAround.getPosition();

            setPosition(new Vector3f(parentPosition).add(relativePositionToParent));
        } else if (this.orbitsAround != null) {
            setPosition(new Vector3f(this.orbitsAround.getPosition()));
        }
    }

}
