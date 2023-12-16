package ru.renett;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class SomHH extends JFrame {

    static final int n = 82000;
    static int N = 0;
    static final int m = 4;
    private static String fname = "/Users/r.tyapkina/Repositories/tsisa/task-8-1/src/main/resources/med.csv";
    static double k[][] = new double[n][m + 2];
    static final int WEIGHT = 20; // pixels
    static final int CELL_COUNT = 20;
    static final int CLASSCOUNT = CELL_COUNT * CELL_COUNT;
    static Node[] nodes = new Node[CLASSCOUNT];

    //trainig parametrs
    static final double SIGMA0 = 10;
    static final double B = 0.1;
    static final double ALPHA0 = 0.01;
    static final double R = 0.1;
    static final int STEP_COUNT = 20;

    private JPanel panel;

    public SomHH() {
        super("Map");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        panel = new JPanel();
        add(panel);

        setSize(WEIGHT * CELL_COUNT, WEIGHT * CELL_COUNT);

        initNode();

        setVisible(true);
    }

    public static void main(String[] args) {
        SomHH knet = new SomHH();
        knet.readCSV();

        knet.main1();
    }

    public void main1() {

        System.out.print("start ... ");

        System.out.println(N + " elements ");

        for (int t = 1; t < STEP_COUNT; t++) {
            System.out.println("Step " + t);
            for (int i = 0; i < N; i++) {
                int index = vectorMinDistance(k[i], nodes);//W*
                for (int j = 0; j < nodes.length; j++) {
                    double rho = rho(nodes[j], nodes[index]);
                    nodes[j].w[0] = nodes[j].w[0] + alpha(t) * h(t, rho)
                            * (k[i][0] - nodes[j].w[0]);

                    nodes[j].w[1] = nodes[j].w[1] + alpha(t) * h(t, rho)
                            * (k[i][1] - nodes[j].w[1]);

                    nodes[j].w[2] = nodes[j].w[2] + alpha(t) * h(t, rho)
                            * (k[i][2] - nodes[j].w[2]);
                }
            }
            repaint();
        }

        System.out.println("end ... ");
    }

    public double alpha(int t) {
        return ALPHA0 * Math.exp(-R * t);
    }

    public double sigma(int t) {
        return SIGMA0 * Math.exp(-B * t);
    }

    public double h(int t, double rho) {
        return Math.exp(-rho / (2 * sigma(t)));
    }

    public double rho(Node node1, Node node2) {
        return Math.sqrt((node1.x - node2.x) * (node1.x - node2.x) +
                (node1.y - node2.y) * (node1.y - node2.y));
    }

    public void initNode() {
        int nodeNum = 0;
        for (int i = 0; i < CELL_COUNT; i++) {
            for (int j = 0; j < CELL_COUNT; j++, nodeNum++) {
                nodes[nodeNum] = new Node();
                nodes[nodeNum].x = j * WEIGHT;
                nodes[nodeNum].y = i * WEIGHT;
                nodes[nodeNum].w[0] = i;
                nodes[nodeNum].w[1] = j;
                nodes[nodeNum].w[2] = i / 2 + j / 2;
            }
        }
        System.out.println("Init complete");
    }

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < CLASSCOUNT; i++) {
            g.setColor(new Color((int) nodes[i].w[0] * 10, (int) nodes[i].w[1] * 10, (int) nodes[i].w[2] * 10));
            g.fillRect((int) nodes[i].x, (int) nodes[i].y, (int) nodes[i].x + WEIGHT, (int) nodes[i].y + WEIGHT);
        }
    }

    public void readCSV() {
        File file = new File(fname);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));) {
            String line = "";
            int i = 0;

            line = br.readLine();
            while ((line = br.readLine()) != null && i < n) {

                String[] elements = line.split(","); // changed from ;
//                System.out.println("Строка " + i + " : " + Arrays.toString(elements));

                // если хотя бы 1 элемент пуст - пропустим эту строку с данными
                if (Arrays.asList(elements).contains("")) continue;

                // Glucose Fasting - 9 index
                // Total Cholesterol - 11
                // Systolic Blood Pressure - 13
                // Diastolic Blood Pressure - 14
                // BMI - 16
                try {
                    k[i][4] = Double.parseDouble(elements[9]);
                    k[i][0] = Double.parseDouble(elements[11]);
                    k[i][1] = Double.parseDouble(elements[13]);
                    k[i][2] = Double.parseDouble(elements[14]);
                    k[i][3] = Double.parseDouble(elements[16]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("ошибка входных данных в строке " + i + " : " + line);
                }
                //System.out.println(k[i][0]+";"+k[i][1]+";"+k[i][2]+";"+k[i][3]+";"+k[i][4]);
            }

            normalizeData();

            N = i;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void normalizeData() {

    }

    public double distance(double[] a, double b[], int dim) {
        if (a == b) return 0;
        double d = 0;
        for (int i = 0; i < dim; i++) {
            d += (a[i] - b[i]) * (a[i] - b[i]);
        }
        //System.out.println(" dst = " + d + " dim = " + dim);

        return d;
    }

    public int vectorMinDistance(double[] a, Node[] v) {
        int index = 0;
        double dst = distance(a, v[0].w, 3);

        for (int i = 1; i < v.length; i++) {
            double d = distance(a, v[i].w, 3);
            if (d < dst) {
                dst = d;
                index = i;
            }
        }

        return index;
    }
}
