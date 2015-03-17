package pl.poznan.put.types;

import java.util.List;

public class UniTypeQuadruplet<T> {
    public final T a;
    public final T b;
    public final T c;
    public final T d;

    public UniTypeQuadruplet(List<T> list) {
        super();
        assert list.size() == 4;
        a = list.get(0);
        b = list.get(1);
        c = list.get(2);
        d = list.get(3);
    }

    public UniTypeQuadruplet(T a, T b, T c, T d) {
        super();
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public UniTypeQuadruplet(T[] array) {
        super();
        assert array.length == 4;
        a = array[0];
        b = array[1];
        c = array[2];
        d = array[3];
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        T a1 = a;
        T b1 = b;
        T c1 = c;
        T d1 = d;
        UniTypeQuadruplet other = (UniTypeQuadruplet) obj;
        if (a1 == null) {
            if (other.a != null) {
                return false;
            }
        } else if (!a1.equals(other.a)) {
            return false;
        }
        if (b1 == null) {
            if (other.b != null) {
                return false;
            }
        } else if (!b1.equals(other.b)) {
            return false;
        }
        if (c1 == null) {
            if (other.c != null) {
                return false;
            }
        } else if (!c1.equals(other.c)) {
            return false;
        }
        if (d1 == null) {
            if (other.d != null) {
                return false;
            }
        } else if (!d1.equals(other.d)) {
            return false;
        }
        return true;
    }

    public T get(int index) {
        assert index >= 0 && index <= 3;

        switch (index) {
        case 0:
            return a;
        case 1:
            return b;
        case 2:
            return c;
        case 3:
            return d;
        default:
            break;
        }

        throw new IllegalArgumentException("UniTypeQuaduplet.get(index) was called with index < 0 or index > 3");
    }

    @Override
    public int hashCode() {
        T a1 = a;
        T b1 = b;
        T c1 = c;
        T d1 = d;

        final int prime = 31;
        int result = 1;
        result = prime * result + (a1 == null ? 0 : a1.hashCode());
        result = prime * result + (b1 == null ? 0 : b1.hashCode());
        result = prime * result + (c1 == null ? 0 : c1.hashCode());
        result = prime * result + (d1 == null ? 0 : d1.hashCode());
        return result;
    }
}
