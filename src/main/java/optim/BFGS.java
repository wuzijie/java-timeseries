package optim;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;

public final class BFGS {
  
  private static final int size = 101;
  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;
  
  private final AbstractMultivariateFunction f;
  private Vector[] iterate = new Vector[size];
  private Vector[] gradient = new Vector[size];
  private Vector[] searchDirection = new Vector[size];
  private double[] stepSize = new double[size];
  private double tol;
  private double[] rho = new double[size];
  private Vector[] s = new Vector[size];
  private Vector[] y = new Vector[size];
  private Matrix[] B = new Matrix[size];
  private Matrix[] H = new Matrix[size];
  private final Matrix identity;
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.tol = tol;
    this.H[0] = initialHessian;
    this.iterate[0] = startingPoint;
    this.stepSize[0] = 1.0;
    int k = 0;
    double functionValue = f.at(startingPoint);
    gradient[k] = f.gradientAt(startingPoint, functionValue);
    while (gradient[k].norm() > tol) {
      searchDirection[k] = (H[k].times(gradient[k]).scaledBy(-1.0));
      iterate[k + 1] = iterate[k].plus(searchDirection[k].scaledBy(stepSize[k]));
      stepSize[k + 1] = updateStepSize(k, functionValue);
      s[k] = iterate[k + 1].minus(iterate[k]);
      functionValue = f.at(iterate[k + 1]);
      gradient[k + 1] = f.gradientAt(iterate[k + 1], functionValue);
      y[k] = gradient[k + 1].minus(gradient[k]);
      rho[k] = 1 / y[k].dotProduct(s[k]);
      H[k + 1] = updateHessian(k);
      k += 1;
    }
  }
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol) {
    this(f, startingPoint, tol, Matrices.identity(startingPoint.size()));
  }
  
  private final double updateStepSize(final int k, final double functionValue) {
    LineSearch lineSearch = new LineSearch(f, functionValue, c1, c2, gradient[k], iterate[k], 
        searchDirection[k], 1.0);
    return lineSearch.computeAlpha();
  }
  
  private final Matrix updateHessian(final int k) {
    Matrix piece1 = identity.minus(s[k].outerProduct(y[k]).scaledBy(rho[k]));
    Matrix piece2 = identity.minus(y[k].outerProduct(s[k]).scaledBy(rho[k]));
    Matrix piece3 = s[k].outerProduct(s[k]).scaledBy(rho[k]);
    return piece1.times(H[k]).times(piece2).plus(piece3);
  }

}