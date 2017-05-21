/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import Optimization.HittingSet;
import algs4.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import stdlib.*;

/**
 *
 * @author Fernando Fossi Expected hash. user evaluated instead to
 * freemarker.template.SimpleScalar on line 13, column 6 in
 * Templates/Classes/Class.java.
 */
public class SBPO2017 {

    public static void main(String[] args) {
        try {
            /* Carregar pasta com arquivos .txt */
//            File directory = new File("D:\\Mestrado\\Projetos\\setcover-grasp\\GraphGenerator\\input\\");
            File directory = new File("D:\\Mestrado\\Projetos\\bitmatrix.git\\bitmatrix\\");

            /* Para cada .txt encontrado */
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith("TOPOLOGY")) {
                    /* inicia */
                    System.out.println("--->Executando arquivo " + file.getName());
                    /* Ler a topologia  */
//                    EdgeWeightedDigraphArtificial graph = new EdgeWeightedDigraphArtificial(new In(file.getAbsolutePath()));
                    EdgeWeightedDigraphChild graph = new EdgeWeightedDigraphChild(new In(file.getAbsolutePath()));

                    /* Aplicar Dijikstra para gerar os caminhos */
                    DijkstraAllPairsSP dijkstra = new DijkstraAllPairsSP(graph);

                    /* Cria o conjunto de nós */
                    HashSet<Integer> nodes = new HashSet<>();
                    for (DirectedEdge edge : graph.edges()) {
                        nodes.add(edge.from());
                        nodes.add(edge.to());
                    }
                    
                    /* Create .dot file */
                    PrintWriter writer = new PrintWriter(file.getName() + ".dot", "UTF-8");
                    writer.println("strict graph 1 {");
                    writer.println("graph[rankdir=\"LR\";pad=\"0.25\"]\n"
                            + "node[target=\"_parent\"]\n"
                            + "edge[fontname=\"CourierNew\"]\n");
                    
                    /* Cria o conjunto de caminhos */
                    Set<Set<Integer>> paths = new HashSet<>();
                    for (int i = 0; i < graph.V(); i++) {
                        for (int j = i + 1; j < graph.V(); j++) {
                            Set<Integer> path = new HashSet<>();
                            for (DirectedEdge edge : dijkstra.path(i, j)) {
                                path.add(edge.from());
                                path.add(edge.to());
                                writer.println(edge.from() + "--" + edge.to());
                            }
                            paths.add(path);
                        }
                    }
                    
                    /* Aplicar Chvatal para resolver */
                    HittingSet hittingSet = new HittingSet(nodes, paths);
                    Set<Integer> O = hittingSet.solve();
                    
                    /* Colher Resultados -> Quantidade de Observadores */
                    System.out.println("--->" + graph.V() + " " + O.size() + " 1 1");
                    
                    /* Verificando nós observadores */
                    for (Integer obs : O) {
                        writer.println(obs + "[style=filled fillcolor=red]");
                    }
                    
                    writer.println("}");
                    writer.close();
                    
                    /* Cria a figura com o caminho */
                    Process p = Runtime.getRuntime().exec("cmd");
                    new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
                    new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
                    PrintWriter stdin = new PrintWriter(p.getOutputStream());
                    stdin.println("dot -Tpng " + file.getName() + ".dot" + " > " + file.getName() + ".png");
                    stdin.close();
                    p.waitFor();
                    
                    System.out.println("\n");
                    
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
