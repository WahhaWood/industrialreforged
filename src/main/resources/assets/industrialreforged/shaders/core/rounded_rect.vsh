#version 150

in vec3 Position;
in vec4 Color;

// Переменные, которые мы передаем в фрагментный шейдер
out vec4 vColor;
out vec2 vLocalPos; // Локальные координаты внутри прямоугольника

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vColor = Color;
    // Передаем локальные координаты (от 0 до width/height)
    vLocalPos = Position.xy;
}