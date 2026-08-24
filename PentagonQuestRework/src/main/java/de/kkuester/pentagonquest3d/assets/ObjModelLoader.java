package de.kkuester.pentagonquest3d.assets;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal OBJ/MTL importer for the specific export style used by Kenney's asset packs:
 * one material per file (a single shared "colormap" texture), triangulated faces only,
 * faces referencing position/texcoord/normal indices. Normals are left to JavaFX's
 * automatic face-normal computation rather than imported explicitly (keeps the format
 * to POINT_TEXCOORD, which TriangleMesh supports directly with two indices per vertex).
 */
public class ObjModelLoader {

    public record LoadedModel(TriangleMesh mesh, PhongMaterial material) {
    }

    public static LoadedModel load(Class<?> resourceAnchor, String basePath, String objFileName) {
        try {
            List<float[]> positions = new ArrayList<>();
            List<float[]> texCoords = new ArrayList<>();
            List<int[]> faceVertices = new ArrayList<>(); // each entry: {posIndex, texIndex}
            String mtlFileName = null;

            String objResource = basePath + "/" + objFileName;
            try (BufferedReader reader = openReader(resourceAnchor, objResource)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    String[] tok = line.split("\\s+");
                    switch (tok[0]) {
                        case "mtllib" -> mtlFileName = tok[1];
                        case "v" -> positions.add(new float[]{
                                Float.parseFloat(tok[1]), Float.parseFloat(tok[2]), Float.parseFloat(tok[3])});
                        case "vt" -> texCoords.add(new float[]{
                                Float.parseFloat(tok[1]), 1f - Float.parseFloat(tok[2])});
                        case "f" -> {
                            for (int i = 1; i <= 3; i++) {
                                String[] parts = tok[i].split("/");
                                int posIdx = Integer.parseInt(parts[0]);
                                int texIdx = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) : 0;
                                faceVertices.add(new int[]{resolveIndex(posIdx, positions.size()),
                                        texIdx == 0 ? -1 : resolveIndex(texIdx, texCoords.size())});
                            }
                        }
                        default -> {
                            // ignore vn, usemtl, g, s, o, etc.
                        }
                    }
                }
            }

            TriangleMesh mesh = new TriangleMesh();
            for (float[] p : positions) {
                mesh.getPoints().addAll(p[0], p[1], p[2]);
            }
            boolean hasTexCoords = !texCoords.isEmpty();
            if (hasTexCoords) {
                for (float[] t : texCoords) {
                    mesh.getTexCoords().addAll(t[0], t[1]);
                }
            } else {
                mesh.getTexCoords().addAll(0f, 0f);
            }
            for (int i = 0; i + 2 < faceVertices.size(); i += 3) {
                for (int v = 0; v < 3; v++) {
                    int[] fv = faceVertices.get(i + v);
                    mesh.getFaces().addAll(fv[0], hasTexCoords && fv[1] >= 0 ? fv[1] : 0);
                }
            }

            PhongMaterial material = mtlFileName != null
                    ? loadMaterial(resourceAnchor, basePath, mtlFileName)
                    : new PhongMaterial(javafx.scene.paint.Color.LIGHTGRAY);

            return new LoadedModel(mesh, material);
        } catch (IOException e) {
            throw new RuntimeException("Konnte OBJ-Modell nicht laden: " + basePath + "/" + objFileName, e);
        }
    }

    private static PhongMaterial loadMaterial(Class<?> anchor, String basePath, String mtlFileName) throws IOException {
        String textureFile = null;
        try (BufferedReader reader = openReader(anchor, basePath + "/" + mtlFileName)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("map_Kd")) {
                    String[] tok = line.split("\\s+");
                    textureFile = tok[tok.length - 1].replace('\\', '/');
                }
            }
        }
        PhongMaterial material = new PhongMaterial(javafx.scene.paint.Color.WHITE);
        if (textureFile != null) {
            URL textureUrl = anchor.getResource(basePath + "/" + textureFile);
            if (textureUrl != null) {
                material.setDiffuseMap(new Image(textureUrl.toExternalForm()));
            }
        }
        return material;
    }

    private static BufferedReader openReader(Class<?> anchor, String resourcePath) throws IOException {
        InputStream in = anchor.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("Ressource nicht gefunden: " + resourcePath);
        }
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    private static int resolveIndex(int objIndex, int count) {
        // OBJ indices are 1-based; negative indices count back from the current end.
        return objIndex > 0 ? objIndex - 1 : count + objIndex;
    }
}
