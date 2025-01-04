package application.portfolio.objects.dao;

public interface DAOConverter<T, D> {
    D toDAO();

    static <T, D> T fromDAO(D object) {
        return null;
    }
}
