package izly;

import org.mockito.Mockito;
import secretCode.CodeBloquéException;
import secretCode.CodeSecret;

public class Purse {

    private double solde;
    private double plafond;
    private int nbOperationsMax;
    private CodeSecret codeSecret;

    private Purse(double plafond, int nbOperationsMax, CodeSecret codeSecret) {
        this.plafond = plafond;
        this.nbOperationsMax = nbOperationsMax;
        this.codeSecret = codeSecret;
    }

    public static Purse createPurse(double plafond, int nbOperationMax, CodeSecret codeSecret) throws CreationPurseException {
        if (nbOperationMax <=0 || plafond <=0 ) throw new CreationPurseException();
        return new Purse(plafond, nbOperationMax, codeSecret);
    }

    public double getSolde() {
        return solde;
    }

    public void debite(double montant, String codeProposé) throws RejetTransactionException {
        try {
            if (!codeSecret.verifierCode(codeProposé))
                throw new RejetTransactionException(new CodeErronnéException());
        } catch (CodeBloquéException e) {
            throw new RejetTransactionException(e);
        }
        prepareTransaction(montant);
        if (montant > solde)
            throw new RejetTransactionException(new SoldeNegatifException());
        solde -= montant;
        postTransaction();
    }

    public void credite(double montant) throws RejetTransactionException {
        prepareTransaction(montant);
        if (montant+solde > plafond)
            throw new RejetTransactionException(new DepassementPlafondException());
        solde += montant;
        postTransaction();
    }

    private void postTransaction() {
        nbOperationsMax--;
    }

    private void prepareTransaction(double montant) throws RejetTransactionException {
        if (nbOperationsMax<=0)
            throw new RejetTransactionException(new NbOperationsMaxAtteindException());
        if (montant <0)
            throw new RejetTransactionException(new MontantNegatifException());
    }

}
