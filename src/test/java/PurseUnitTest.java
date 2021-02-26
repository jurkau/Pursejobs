import izly.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import secretCode.CodeBloqueException;
import secretCode.CodeSecret;

import java.util.Random;

import static org.hamcrest.core.IsInstanceOf.instanceOf;


public class PurseUnitTest {

    private Purse purse;
    private CodeSecret codeSecret;
    private double plafond;
    private int operationMax;

    @Before
    public void setup() throws CreationPurseException, CodeBloqueException {
        codeSecret = Mockito.mock(CodeSecret.class);
        Mockito.when(codeSecret.verifierCode("9876")).thenReturn(true);
        plafond = 100;
        operationMax = 500;
        purse = Purse.createPurse(plafond, operationMax, codeSecret);
    }

    @Test
    public void testDebite() throws Exception {
        purse.credite(50);
        double solde = purse.getSolde();
        purse.debite(50, "9876");
        Assert.assertEquals(solde - 50, purse.getSolde(), 0);
    }

    @Test
    public void testCredite() throws Exception {
        double solde = purse.getSolde();
        purse.credite(50);
        Assert.assertEquals(solde + 50, purse.getSolde(), 0);
    }

    @Rule
    public ExpectedException ecouteur = ExpectedException.none();

    @Test
    public void testSoldeJamaisNegatif() throws Exception {
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(SoldeNegatifException.class));
        purse.debite(purse.getSolde() + 1, "9876");
    }

    @Test(expected = RejetTransactionException.class)
    public void testSoldeToujoursInferieurAuPlafondMisALaCreationDuPurse() throws Exception {
        purse.credite(plafond + 1);
    }

    @Test
    public void testSoldeVraimentToujoursInferieurAuPlafondMisALaCreationDuPurse() throws Exception {
        purse.credite(plafond / 2);
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(DepassementPlafondException.class));
        purse.credite(plafond / 2 + 1);
    }

    @Test
    public void testCreditNegatif() throws Exception {
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(MontantNegatifException.class));
        purse.credite(-50);
    }

    @Test
    public void testDebitNegatif() throws Exception {
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(MontantNegatifException.class));
        purse.debite(-50, "9876");
    }

    @Test
    public void testNbOperationsMaxAtteindSurCredit() throws Exception {
        Purse purse = Purse.createPurse(plafond, 2, codeSecret);
        purse.credite(10);
        purse.credite(10);
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(NbOperationsMaxAtteindException.class));
        purse.credite(10);
    }

    @Test
    public void testNbOperationsMaxAtteindSurDebit() throws Exception {
        Purse purse = Purse.createPurse(plafond, 2, codeSecret);
        purse.credite(60);
        purse.debite(10, "9876");
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(NbOperationsMaxAtteindException.class));
        purse.debite(10, "9876");
    }

    @Test (expected = CreationPurseException.class)
    public void testNbOperationMaxNegatifRejete() throws Exception{
        Purse.createPurse(100, -5, new CodeSecret(new Random()));
    }
    @Test (expected = CreationPurseException.class)
    public void testPlafondNegatifRejete() throws Exception{
        Purse.createPurse(-100, 5, new CodeSecret(new Random()));
    }

    @Test
    public void testDebitRejetéSurCodeIncorrect() throws Exception {
        purse.credite(50);
        ecouteur.expect(RejetTransactionException.class);
        ecouteur.expectCause(instanceOf(CodeErronnéeException.class));
        purse.debite(30, "1234");
    }
}
