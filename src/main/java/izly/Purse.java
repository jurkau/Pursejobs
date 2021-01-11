package izly;

import org.mockito.Mockito;
import secretCode.CodeSecret;

public class Purse {

    public static Purse purse;

    private Purse() {
    }

    public static Purse createPurse(double plafond, int nbOperationMax, CodeSecret codeSecret) throws CreationPurseException {
        purse = Mockito.mock(Purse.class);
        try {
            Mockito.doThrow(new RejetTransactionException()).when(purse).debite(Mockito.anyDouble());
        } catch (RejetTransactionException e) {
            e.printStackTrace();
        }

    }

    public double getSolde() {
        return 0;
    }

    public void debite(double montant, String codeProposé) throws RejetTransactionException {
    }

    public void credite(double montant) throws RejetTransactionException {
    }

}
