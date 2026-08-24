package de.kkuester.pentagonquest3d.assets;

import javafx.collections.ObservableFloatArray;
import javafx.collections.ObservableIntegerArray;
import javafx.scene.shape.TriangleMesh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjModelLoaderTest {

    @Test
    void loadsQuadFixture_withCorrectVertexAndFaceCounts() {
        ObjModelLoader.LoadedModel model = ObjModelLoader.load(
                ObjModelLoaderTest.class, "/de/kkuester/pentagonquest3d/testmodels", "quad.obj");

        TriangleMesh mesh = model.mesh();
        ObservableFloatArray points = mesh.getPoints();
        ObservableFloatArray texCoords = mesh.getTexCoords();
        ObservableIntegerArray faces = mesh.getFaces();

        assertEquals(4 * 3, points.size(), "4 Vertices * xyz");
        assertEquals(4 * 2, texCoords.size(), "4 Texcoords * uv");
        assertEquals(2 * 3 * 2, faces.size(), "2 Dreiecke * 3 Ecken * (pos+tex)-Index");
    }

    @Test
    void loadsQuadFixture_firstTrianglePointsAtExpectedPositions() {
        ObjModelLoader.LoadedModel model = ObjModelLoader.load(
                ObjModelLoaderTest.class, "/de/kkuester/pentagonquest3d/testmodels", "quad.obj");

        ObservableFloatArray points = model.mesh().getPoints();
        assertEquals(0f, points.get(0), 0.0001f);
        assertEquals(0f, points.get(1), 0.0001f);
        assertEquals(0f, points.get(2), 0.0001f);
        assertEquals(1f, points.get(3), 0.0001f); // zweiter Vertex: (1,0,0)
    }

    @Test
    void loadsQuadFixture_faceIndicesAreZeroBased() {
        ObjModelLoader.LoadedModel model = ObjModelLoader.load(
                ObjModelLoaderTest.class, "/de/kkuester/pentagonquest3d/testmodels", "quad.obj");

        ObservableIntegerArray faces = model.mesh().getFaces();
        // f 1/1 2/2 3/3 -> zero-based: 0,0, 1,1, 2,2
        assertEquals(0, faces.get(0));
        assertEquals(0, faces.get(1));
        assertEquals(1, faces.get(2));
        assertEquals(1, faces.get(3));
        assertEquals(2, faces.get(4));
        assertEquals(2, faces.get(5));
    }

    @Test
    void gracefullyFallsBackToDefaultMaterial_whenTextureFileIsMissing() {
        ObjModelLoader.LoadedModel model = ObjModelLoader.load(
                ObjModelLoaderTest.class, "/de/kkuester/pentagonquest3d/testmodels", "quad.obj");

        assertNotNull(model.material(), "sollte trotz fehlender Textur ein Material liefern");
        assertNull(model.material().getDiffuseMap(), "keine Textur-Datei vorhanden -> diffuseMap bleibt leer");
    }

    @Test
    void missingObjFile_throwsRuntimeException() {
        assertThrows(RuntimeException.class, () -> ObjModelLoader.load(
                ObjModelLoaderTest.class, "/de/kkuester/pentagonquest3d/testmodels", "does-not-exist.obj"));
    }
}
