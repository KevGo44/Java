import sas.*;

public class Ufo
{
    private Picture ufobild;
    private Picture explosion;
    private Asteroid[] asteroidenObejekt;
    private Laser laserObjekt;
    private boolean death;
    private boolean shoot;
    private Sound laserShoot;
    private Sound explosionUfo;
    
    public Ufo(Asteroid[] pAsteroiden, Laser pLaser)
    {
        ufobild = new Picture(400/2-45/2,500,60,60,"material/rakete2.png");
        explosion = new Picture(0,-100,"material/explosion.png");
        explosion.setHidden(true);
        asteroidenObejekt = pAsteroiden;
        laserObjekt = pLaser;
        death = false;
        shoot = false;
        laserShoot = new Sound("material/fire.wav");
        explosionUfo = new Sound("material/bangLarge.wav");
    }
    
    public void setHiddenTrue()
    {
        ufobild.setHidden(true);
    }
    
    public void setHiddenFalse()
    {
        ufobild.setHidden(false);
    }
    
    public void explosionHiddenTrue()
    {
        explosion.setHidden(true);
    }
    
    public void bewegeLinks(double pGeschw)
    {
        if (ufobild.getShapeX() > 1)
        {
            ufobild.move(-pGeschw,0);
        }
    }

    public void bewegeRechts(double pGeschw)
    {
        if (ufobild.getShapeX() < 400-60/2)
        {
            ufobild.move(pGeschw,0);
        }
    }
    
    public void bewegeVorne(double pGeschw)
    {
        if (ufobild.getShapeY() > 1)
        {
            ufobild.move(0,-pGeschw);
        }
    }

    public void bewegeHinten(double pGeschw)
    {
        if (ufobild.getShapeY() < 600-60)
        {
            ufobild.move(0,pGeschw);
        }
    }     
    
    public boolean checkKill()
    {
        for (int i = 0; i < asteroidenObejekt.length; i++)
        {
            if (asteroidenObejekt[i].gibStein().intersects(ufobild))
            {
                death = true;
                return true;
            }
        }
        return false;
    }
    
    public boolean death()
    {
        if (death == true)
        {
            return true;
        } else
        {
            return false;
        }
    }
    
    public void explodiere()
    {
        ufobild.setHidden(true);
        explosion.moveTo(ufobild.getShapeX()-(85-45)/2,ufobild.getShapeY());
        explosion.setHidden(false);
        explosionUfo.play();
    }
    
    public void schuss()
    {
        laserObjekt.moveTo(ufobild.getCenterX()+9, ufobild.getCenterY()-10);
        laserShoot.play();
        shoot = true;
    }
    
    public void shootFalse()
    {
        shoot = false;
    }
    
    public boolean shoot()
    {
        if (shoot)
        {
            return true;
        }else
        {
            return false;
        }
    }
    
    public void reset()
    {
        ufobild.reset();
        explosion.reset();
        death = false;
    }
}