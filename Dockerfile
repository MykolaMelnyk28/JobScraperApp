FROM maven:3.9.6-amazoncorretto-21 AS builder

COPY . /app/.

WORKDIR /app

RUN mvn clean package

FROM selenium/standalone-chrome:124.0-20250606 AS selenium

FROM amazoncorretto:21

COPY --from=builder /app/target/*.jar /app/app.jar

COPY --from=selenium /opt/google/chrome /opt/google/chrome
COPY --from=selenium /usr/bin/chromedriver /usr/bin/chromedriver

RUN yum install -y \
    alsa-lib \
    atk \
    cups-libs \
    dbus-glib \
    gtk3 \
    libXcomposite \
    libXcursor \
    libXdamage \
    libXext \
    libXi \
    libXrandr \
    libXScrnSaver \
    libXtst \
    pango \
    xorg-x11-fonts-100dpi \
    xorg-x11-fonts-75dpi \
    xorg-x11-utils \
    xorg-x11-fonts-cyrillic \
    xorg-x11-fonts-Type1 \
    xorg-x11-fonts-misc \
    && yum clean all

ENV PATH="/opt/google/chrome:${PATH}"
ENV PATH="/usr/bin:${PATH}"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
