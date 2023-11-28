package cl.kanopus.common.data;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 */
public class ImageBase64 {

    private String data;

    public ImageBase64() {
    }

    public ImageBase64(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return (data != null) ? data : super.toString();
    }

}
