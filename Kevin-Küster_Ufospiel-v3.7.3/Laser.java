import sas.*;

public class Laser
{
    private Picture laserbild;
    private Asteroid[] asteroidenObjekt;
    private boolean freeToFire;
    private boolean shoot;
    private boolean pDestAst;
    private Sound explosionAst;
    
    public Laser(Asteroid[] pAsteroiden)
    {
        laserbild = new Picture(0,-100,40,80,"material/laser.png");
        asteroidenObjekt = pAsteroiden;
        freeToFire = true;
        shoot = true;
        pDestAst = false;
        explosionAst = new Sound("material/bangSmall.wav");
    }
    
    public void setHiddenTrue()
    {
        laserbild.setHidden(true);
    }
    
    public void setHiddenFalse()
    {
        laserbild.setHidden(false);
    }
    
    public void moveTo(double pX, double pY)
    {
        laserbild.moveTo(pX, pY);
    }
    
    public boolean intersected()
    {
        boolean destroy = false;
        for (int i = 0; i < asteroidenObjekt.length; i++)
        {
            if (asteroidenObjekt[i].gibStein().intersects(laserbild))
            {
                destroy = true;
                return true;
            }
        }
        return false;
    }
    
    public void move(double pGeschw)
    {
        freeToFire = false;
        laserbild.move(0,-pGeschw);
        if (laserbild.getCenterY() < 0)
        {
            laserbild.reset();
            freeToFire = true;
        }else
        {
            for (int i = 0; i < asteroidenObjekt.length; i++)
            {
                if (asteroidenObjekt[i].gibStein().intersects(laserbild))
                {
                    asteroidenObjekt[i].setzeZurueck(-40);
                    laserbild.reset();
                    explosionAst.playUltimate();
                    pDestAst = true;
                    freeToFire = true;
                }
            }
        }
    }
    
    public boolean freeToFire()
    {
        if (freeToFire == true)
        {
            return true;
        }else
        {
            return false;
        }
    }
    
    public void pDestAstFalse()
    {
        pDestAst = false;
    }
    
    public boolean pDestAst()
    {
        if (pDestAst == true)
        {
            return true;
        }else
        {
            return false;
        }
    }
}