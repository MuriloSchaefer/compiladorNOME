/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiladornome;

/**
 *
 * @author schaefer
 */
public class Triple<F, S, T> {

    public F first;
    public S second;
    public T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple<?, ?, ?> p = (Triple<?, ?, ?>) o;
        return first.equals(p.first) && second.equals(p.second) && third.equals(p.third);
    }

    private static boolean equals(Object x, Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode()) ^ (third == null ? 0 : third.hashCode());
    }

    @Override
    public String toString() {
        return "(" +first + ", " + second + ", " + third + ')';
    }
    

    public static <F, S, T> Triple <F, S, T> create(F f, S s, T t) {
        return new Triple<F, S, T>(f, s, t);
    }
}
