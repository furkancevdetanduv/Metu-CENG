#include <iostream>
#include "parser.h"
#include "ppm.h"
#include <cmath>
#include <cfloat>

typedef struct
{
  parser::Vec3f a;
  parser::Vec3f b;
} Ray;

typedef struct
{
  float t;
  int objectType;
  int objectFace;
  int objectIndex;
} Intersection;

typedef struct
{
  parser::Vec3f position;
  parser::Vec3f gaze;
  parser::Vec3f up;
  parser::Vec3f right;
  parser::Vec4f np;
  float near_distance;
  int width, height, byte;
  std::string name;
  parser::Vec3f qx;
  parser::Vec3f qy;
  parser::Vec3f p1m;
} myCamera;

parser::Vec3f mult(parser::Vec3f a, float c) {
  parser::Vec3f tmp{
      .x = a.x * c,
      .y = a.y * c,
      .z = a.z * c
  };

  return tmp;
}

parser::Vec3f div(parser::Vec3f a, float c) {
  parser::Vec3f tmp{
      .x = a.x / c,
      .y = a.y / c,
      .z = a.z / c
  };

  return tmp;
}

parser::Vec3f add(parser::Vec3f a, parser::Vec3f b) {
  parser::Vec3f tmp = {
      .x = a.x+b.x,
      .y = a.y+b.y,
      .z = a.z+b.z
  };

  return tmp;
}

parser::Vec3f sub(parser::Vec3f a, parser::Vec3f b) {
  parser::Vec3f tmp = {
      .x = a.x-b.x,
      .y = a.y-b.y,
      .z = a.z-b.z
  };

  return tmp;
}

float dot(parser::Vec3f a, parser::Vec3f b) {
  return a.x*b.x+a.y*b.y+a.z*b.z;
}

parser::Vec3f cross(parser::Vec3f a, parser::Vec3f b) {
  parser::Vec3f tmp{
      .x = a.y*b.z-b.y*a.z,
      .y = b.x*a.z-a.x*b.z,
      .z = a.x*b.y-b.x*a.y
  };

  return tmp;
}

float det(float a11,float a12,float a13,
          float a21,float a22,float a23,
          float a31,float a32,float a33) {
  return (a11 * ((a22 * a33) - (a32 * a23))) -
         (a21 * ((a33 * a12) - (a13 * a32))) +
         (a31 * ((a12 * a23) - (a13 * a22)));
}

float length(parser::Vec3f v)
{
  return sqrtf(v.x*v.x+v.y*v.y+v.z*v.z);
}





parser::Vec3f normalize(parser::Vec3f v) {
  float d = length(v);
  parser::Vec3f tmp{
      .x = v.x/d,
      .y = v.y/d,
      .z = v.z/d
  };

  return tmp;
}






void printVector(const char message[], parser::Vec3f v){
  std::cout<<message<<" : "<<v.x<<" "<<v.y<<" "<<v.z<<std::endl;
}

class RayTracer{
  parser::Scene* scene;
  myCamera cc;
  int cameraCount;
  int pointLightCount;
  int materialCount;
  int vertexCount;
  int meshCount;
  int triangleCount;
  int sphereCount;
public:
  RayTracer(parser::Scene* myScene) {
    scene = myScene;
    cameraCount = scene->cameras.size();
    pointLightCount = scene->point_lights.size();
    materialCount = scene->materials.size();
    vertexCount = scene->vertex_data.size();
    meshCount = scene->meshes.size();
    triangleCount = scene->triangles.size();
    sphereCount = scene->spheres.size();
  }
  parser::Camera* getCamera(int c) {
    return &scene->cameras[c];
  }
  parser::PointLight* getPointLight(int p) {
    return &scene->point_lights[p];
  }
  parser::Material* getMaterial(int m) {
    return &scene->materials[m];
  }
  parser::Vec3f* getVertex(int v) {
    return &scene->vertex_data[v];
  }
  parser::Mesh* getMesh(int m) {
    return &scene->meshes[m];
  }
  parser::Triangle* getTriangle(int t) {
    return &scene->triangles[t];
  }
  parser::Sphere* getSphere(int s) {
    return &scene->spheres[s];
  }
  Ray generateRay(int i, int j){
    Ray tmp;

    tmp.a = cc.position;

    parser::Vec3f pij = add(add(cc.p1m, mult(cc.qx, (float)i)), mult(cc.qy, (float)j));

    tmp.b = normalize(pij);

    return tmp;
  }
  void setCurrentCamera(parser::Camera* camera){
    cc = {
        .position = camera->position,
        .gaze = normalize(camera->gaze),
        .up = normalize(camera->up),
        .right = normalize(cross(camera->up, camera->gaze)),
        .np = camera->near_plane,
        .near_distance = camera->near_distance,
        .width = camera->image_width,
        .height = camera->image_height,
        .byte = camera->image_width * camera->image_height * 3,
        .name = camera->image_name
    };
    float planeWidth = cc.np.y-cc.np.x;
    float planeHeight = cc.np.w-cc.np.z;
    float pixelWidth = planeWidth/(float)cc.width;
    float pixelHeight = planeHeight/(float)cc.height;
    float hx = planeWidth - pixelWidth;
    float hy = planeHeight - pixelHeight;
    float gx = hx/(float)2;
    float gy = hy/(float)2;
    parser::Vec3f qx = mult(cc.right, hx/(float)cc.width);
    parser::Vec3f qy = mult(cc.up, hy/(float)cc.height);
    parser::Vec3f p1m = sub(sub(mult(cc.gaze, cc.near_distance), mult(cc.right, gx)), mult(cc.up, gy));
    cc.qx = qx;
    cc.qy = qy;
    cc.p1m = p1m;
  }
  float calculateTriangleIntersection(parser::Face &face, Ray ray, float tresHold){
    parser::Vec3f* p1 = getVertex(face.v0_id-1);
    parser::Vec3f* p2 = getVertex(face.v1_id-1);
    parser::Vec3f* p3 = getVertex(face.v2_id-1);

    float calculatedDet = det(
        p1->x - p2->x, p1->x - p3->x, ray.b.x,
        p1->y - p2->y, p1->y - p3->y, ray.b.y,
        p1->z - p2->z, p1->z - p3->z, ray.b.z);

//    if(det < pScene->intTestEps && det > -pScene->intTestEps)
//      return -1;

    float beta = det(
        p1->x - ray.a.x, p1->x - p3->x, ray.b.x,
        p1->y - ray.a.y, p1->y - p3->y, ray.b.y,
        p1->z - ray.a.z, p1->z - p3->z, ray.b.z)
                 / calculatedDet;
    float gamma = det(
        p1->x - p2->x, p1->x - ray.a.x, ray.b.x,
        p1->y - p2->y, p1->y - ray.a.y, ray.b.y,
        p1->z - p2->z, p1->z - ray.a.z, ray.b.z)
                  / calculatedDet;
    float t = det(
        p1->x - p2->x, p1->x - p3->x, p1->x - ray.a.x,
        p1->y - p2->y, p1->y - p3->y, p1->y - ray.a.y,
        p1->z - p2->z, p1->z - p3->z, p1->z - ray.a.z)
              / calculatedDet;

    if (t > tresHold && beta + gamma <= 1 && 0 <= beta && 0 <= gamma)
      return t;
//    if (t > pScene->intTestEps && beta + gamma <= 1 && 0 <= beta && 0 <= gamma)
//      return t;

    return -1;
  }
  float calculateSphereIntersection(parser::Sphere* sphere, Ray &ray, float tresHold){
    parser::Vec3f* center = getVertex(sphere->center_vertex_id-1);
    parser::Vec3f OminusC = sub(ray.a, *center);
    float A = dot(ray.b, ray.b);
    float B = 2 * dot(ray.b, OminusC);
    float C = dot(OminusC, OminusC) - sphere->radius * sphere->radius;
    double delta = B * B - 4 * A * C;
    if ( delta < 0 ){
      return -1;
    }
    float t1 = ( - B + (float)sqrt(delta) ) / (2 * A);
    float t2 = ( - B - (float)sqrt(delta) ) / (2 * A);
    if ( t1 < t2 && t1 >= tresHold ){
      return t1;
    } else if ( t2 >= tresHold ) {
      return t2;
    }
    return -1;
  }
  Intersection calculateClosestIntersection(Ray &ray, float tresHold){
    Intersection temp = { .t=FLT_MAX, .objectType=-1, .objectIndex=-1};
    float newT;
    for (int s=0; s < sphereCount; s++){
      newT = calculateSphereIntersection(getSphere(s), ray, tresHold);
      if ( newT >= tresHold) {
        if ( newT < temp.t ) {
          temp.t = newT;
          temp.objectIndex = s;
          temp.objectFace = -1;
          temp.objectType = 1;
        }
      }
    }
    for (int t=0; t < triangleCount; t++){
      newT = calculateTriangleIntersection(getTriangle(t)->indices, ray, tresHold);
      if ( newT >= tresHold) {
        if ( newT < temp.t ) {
          temp.t = newT;
          temp.objectIndex = t;
          temp.objectFace = -1;
          temp.objectType = 2;
        }
      }
    }
    for (int m=0; m < meshCount; m++){
      for(int f=0; f < getMesh(m)->faces.size(); f++) {
        newT = calculateTriangleIntersection(getMesh(m)->faces[f], ray, tresHold);
        if (newT >= tresHold) {
          if (newT < temp.t) {
            temp.t = newT;
            temp.objectIndex = m;
            temp.objectFace = f;
            temp.objectType = 3;
          }
        }
      }
    }
    if(temp.objectType == -1){
      return {
        .t=-1, .objectType=-1, .objectFace=-1, .objectIndex=-1
      };
    }
    return temp;
  }
  int getMaterialIdFromIntersection(Intersection &intersection){
    if(intersection.objectType == 1){
      return getSphere(intersection.objectIndex)->material_id-1;
    }else if(intersection.objectType == 2){
      return getTriangle(intersection.objectIndex)->material_id-1;
    }else if(intersection.objectType == 3){
      return getMesh(intersection.objectIndex)->material_id-1;
    }else{
      return -1;
    }
  }
  void updateColorWithAmbient(parser::Vec3f &color, Intersection &intersection){
    int materialId = getMaterialIdFromIntersection(intersection);
    color.x = getMaterial(materialId)->ambient.x * scene->ambient_light.x;
    color.y = getMaterial(materialId)->ambient.y * scene->ambient_light.y;
    color.z = getMaterial(materialId)->ambient.z * scene->ambient_light.z;
  }
  parser::Vec3f getNormalOfIntersectedObject(Intersection intersection, parser::Vec3f intersectedPoint) {
    if (intersection.objectType == 1) {
      return sub(intersectedPoint, *getVertex(scene->spheres[intersection.objectIndex].center_vertex_id - 1));
    } else if (intersection.objectType == 2) {
      parser::Triangle* tri = getTriangle(intersection.objectIndex);
      parser::Vec3f* v0 = getVertex(tri->indices.v0_id-1);
      parser::Vec3f* v1 = getVertex(tri->indices.v1_id-1);
      parser::Vec3f* v2 = getVertex(tri->indices.v2_id-1);
      return cross(sub(*v2, *v1), sub(*v0, *v1));
    } else if (intersection.objectType == 3) {
      parser::Mesh* mesh = getMesh(intersection.objectIndex);
      parser::Vec3f* v0 = getVertex(mesh->faces[intersection.objectFace].v0_id-1);
      parser::Vec3f* v1 = getVertex(mesh->faces[intersection.objectFace].v1_id-1);
      parser::Vec3f* v2 = getVertex(mesh->faces[intersection.objectFace].v2_id-1);
      return cross(sub(*v2, *v1), sub(*v0, *v1));
    } else {
      return {0,0,0};
    }
  }


  void calculateColorWithReflection(parser::Vec3f &color, int i, parser::Material* oldMaterial, parser::Vec3f point, parser::Vec3f &w0, parser::Vec3f &n) {
    if(i >= scene->max_recursion_depth){
      return;
    }
    parser::Vec3f tempColor = {
        .x=(float)scene->background_color.x,
        .y=(float)scene->background_color.y,
        .z=(float)scene->background_color.z
    };
    parser::Vec3f wr = sub(mult(n, 2 * dot(w0, n)), w0);
    parser::Vec3f wrNormal = normalize(wr);
    Intersection closestIntersection, closestShadowIntersection;
    Ray r = { .a=add(point, mult(wrNormal, scene->shadow_ray_epsilon)), .b=wrNormal };
//    Ray r = { .a=add(point, mult(wrNormal, scene->shadow_ray_epsilon)), .b=wrNormal };
//        printVector("AAA", r.a);
//        printVector("BBB", r.b);
    closestIntersection = calculateClosestIntersection(r, 0);
//        std::cout<<i<<" "<<j<<" "<<closestIntersection.t<<" "<<closestIntersection.objectType<<" "<<closestIntersection.objectIndex<<std::endl;

    if( closestIntersection.t != -1 ){
      updateColorWithAmbient(tempColor, closestIntersection);

      int materialId = getMaterialIdFromIntersection(closestIntersection);
      parser::Material* material = getMaterial(materialId);

      parser::Vec3f intersectionPoint = add(r.a, mult(r.b, closestIntersection.t));

      parser::Vec3f nNew = getNormalOfIntersectedObject(closestIntersection, intersectionPoint);
      parser::Vec3f normalizedN = normalize(nNew);

      parser::Vec3f wi = {0,0,0}; // point to light vector
      parser::Vec3f normalizedWi = {0,0,0};

      Ray shadowRay;

      parser::Vec3f receivedIrradianceNew = {0,0,0};
      parser::Vec3f diffuseContribution = {0,0,0};
      parser::Vec3f h = {0,0,0};
      parser::Vec3f specularContribution = {0,0,0};
      parser::Vec3f w0new = {0,0,0};

      for(int l=0; l<pointLightCount; l++) {
        wi = sub(getPointLight(l)->position, intersectionPoint); // point to light vector
        normalizedWi = normalize(wi);

        shadowRay = {
            .a = add(intersectionPoint, mult(normalize(wi), scene->shadow_ray_epsilon)),
            .b = normalizedWi
        };
        closestShadowIntersection = calculateClosestIntersection(shadowRay, 0);
        if(closestShadowIntersection.objectType != -1 && closestShadowIntersection.t <= length(wi)){
          continue;
        }

        float cosTheta = dot(normalizedWi, normalizedN);
        if(cosTheta < 0) cosTheta = 0;
        float lightDistance = length(wi);
        receivedIrradianceNew = div(getPointLight(l)->intensity, lightDistance * lightDistance);
        diffuseContribution = mult(receivedIrradianceNew, cosTheta);
        tempColor.x = tempColor.x + diffuseContribution.x * material->diffuse.x;
        tempColor.y = tempColor.y + diffuseContribution.y * material->diffuse.y;
        tempColor.z = tempColor.z + diffuseContribution.z * material->diffuse.z;

        h = sub(wi, mult(r.b, closestIntersection.t));
        float cosAlpha = dot(normalizedN, normalize(h));
        if(cosAlpha<0) cosAlpha = 0;
        cosAlpha = powf(cosAlpha, material->phong_exponent);
        specularContribution = mult(receivedIrradianceNew, cosAlpha);
        tempColor.x = tempColor.x + specularContribution.x * material->specular.x;
        tempColor.y = tempColor.y + specularContribution.y * material->specular.y;
        tempColor.z = tempColor.z + specularContribution.z * material->specular.z;


      }
      w0new = mult(r.b, -1);
      if(material->mirror.x != 0 || material->mirror.y != 0 || material->mirror.z != 0) {
        calculateColorWithReflection(tempColor, i + 1, material, intersectionPoint, w0new, normalizedN);
      }

      tempColor.x = tempColor.x * oldMaterial->mirror.x;
      tempColor.y = tempColor.y * oldMaterial->mirror.y;
      tempColor.z = tempColor.z * oldMaterial->mirror.z;
      color.x = color.x + (tempColor.x);
      color.y = color.y + (tempColor.y);
      color.z = color.z + (tempColor.z);
//          a=1;
    }
  }


  void calculateImage(unsigned char* image) {
    Ray r;
    parser::Vec3f color{
        .x = (float)scene->background_color.x,
        .y = (float)scene->background_color.y,
        .z = (float)scene->background_color.z
    };
    int pixelIndex;
    Intersection closestIntersection;
    Intersection closestShadowIntersection;
//    int a = 0;
    parser::Vec3f wi = {0,0,0}; // point to light vector
    parser::Vec3f wiNormalized = {0,0,0};

    parser::Material* material = nullptr;

    parser::Vec3f intersectionPoint = {0,0,0};

    parser::Vec3f n = {0,0,0};
    parser::Vec3f normalizedN = {0,0,0};

    Ray shadowRay;

    parser::Vec3f receivedIrradiance = {0,0,0};
    parser::Vec3f diffuseContribution = {0,0,0};

    parser::Vec3f h = {0,0,0};

    parser::Vec3f w0 = {0,0,0};
    parser::Vec3f specularContribution = {0,0,0};
    for (int i = 0; i < cc.width; i++) {
      for (int j = 0; j < cc.height; j++) {
        color.x = (float)scene->background_color.x;
        color.y = (float)scene->background_color.y;
        color.z = (float)scene->background_color.z;
        r = generateRay(i, j);
//        printVector("AAA", r.a);
//        printVector("BBB", r.b);
        closestIntersection = calculateClosestIntersection(r, cc.near_distance);
//        std::cout<<i<<" "<<j<<" "<<closestIntersection.t<<" "<<closestIntersection.objectType<<" "<<closestIntersection.objectIndex<<std::endl;

        if( closestIntersection.t != -1 ){
          updateColorWithAmbient(color, closestIntersection);

          int materialId = getMaterialIdFromIntersection(closestIntersection);
          material = getMaterial(materialId);

          intersectionPoint = add(r.a, mult(r.b, closestIntersection.t));

          n = getNormalOfIntersectedObject(closestIntersection, intersectionPoint);
          normalizedN = normalize(n);

          for(int l=0; l<pointLightCount; l++) {
            wi = sub(getPointLight(l)->position, intersectionPoint); // point to light vector
            wiNormalized = normalize(wi);

            shadowRay = {
                .a = add(intersectionPoint, mult(wiNormalized, scene->shadow_ray_epsilon)),
                .b = wiNormalized
            };
            closestShadowIntersection = calculateClosestIntersection(shadowRay, 0);
            if(closestShadowIntersection.objectType != -1 && closestShadowIntersection.t <= length(wi)){
              continue;
            }

            float cosTheta = dot(wiNormalized, normalizedN);
            if(cosTheta < 0) cosTheta = 0;
            float lightDistance = length(wi);
            receivedIrradiance = div(getPointLight(l)->intensity, lightDistance * lightDistance);
            diffuseContribution = mult(receivedIrradiance, cosTheta);
            color.x = color.x + diffuseContribution.x * material->diffuse.x;
            color.y = color.y + diffuseContribution.y * material->diffuse.y;
            color.z = color.z + diffuseContribution.z * material->diffuse.z;

            h = sub(wi, mult(r.b, closestIntersection.t));
            float cosAlpha = dot(normalizedN, normalize(h));
            if(cosAlpha<0) cosAlpha = 0;
            cosAlpha = powf(cosAlpha, material->phong_exponent);
            specularContribution = mult(receivedIrradiance, cosAlpha);
            color.x = color.x + specularContribution.x * material->specular.x;
            color.y = color.y + specularContribution.y * material->specular.y;
            color.z = color.z + specularContribution.z * material->specular.z;

          }
          w0 = mult(r.b, -1);
          if(material->mirror.x != 0 || material->mirror.y != 0 || material->mirror.z != 0) {
            calculateColorWithReflection(color, 0, material, intersectionPoint, w0, normalizedN);
          }


//          a=1;
        }
        pixelIndex = ((cc.height-j-1) * cc.width + (cc.width - i-1)) * 3;
//        std::cout<<i<<" "<<j<<" "<<closestIntersection.t<<" "<<closestIntersection.objectType<<" "<<closestIntersection.objectIndex<<std::endl;
//        std::cout<<pixelIndex<<" "<<pixelIndex+1<<" "<<pixelIndex+2<<" / "<<cc.width*cc.height*3<<" "<<std::endl;
//        printVector("Color", color);
        image[pixelIndex] = (unsigned char)(color.x > 255 ? 255 : (color.x < 0 ? 0 : color.x));
//        std::cout<<pixelIndex<<" / "<<cc.width*cc.height*3<<" "<<std::endl;
        image[pixelIndex+1] = (unsigned char)(color.y > 255 ? 255 : (color.y < 0 ? 0 : color.y));
//        std::cout<<pixelIndex+1<<" / "<<cc.width*cc.height*3<<" "<<std::endl;
        image[pixelIndex+2] = (unsigned char)(color.z > 255 ? 255 : (color.z < 0 ? 0 : color.z));
//        std::cout<<pixelIndex+2<<" / "<<cc.width*cc.height*3<<" "<<std::endl;
//        if(a==1){
//          break;
//        }
      }
//      if(a==1){
//        break;
//      }
    }
  }
  void writeAllImages() {
    for(int c = 0; c < cameraCount; c++){
      setCurrentCamera(getCamera(c));
      auto* image = new unsigned char [cc.byte];
      calculateImage(image);
      write_ppm(cc.name.c_str(), image, cc.width, cc.height);
    }
  }
};

int main(int argc, char* argv[])
{
  // Sample usage for reading an XML scene file
  parser::Scene scene;

  scene.loadFromXml(argv[1]);

  // The code below creates a test pattern and writes
  // it to a PPM file to demonstrate the usage of the
  // ppm_write function.
  //
  // Normally, you would be running your ray tracing
  // code here to produce the desired image.

  RayTracer myRayTracer(&scene);
  myRayTracer.writeAllImages();
}