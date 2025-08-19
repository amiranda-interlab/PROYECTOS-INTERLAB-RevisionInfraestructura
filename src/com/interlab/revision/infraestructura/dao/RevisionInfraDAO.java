package com.interlab.revision.infraestructura.dao;

import com.interlab.revision.infraestructura.bean.RevisionInfra;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RevisionInfraDAO {

    public List<RevisionInfra> obtenerBasesActivas(Connection con) throws Exception {
        List<RevisionInfra> lista = new ArrayList<>();

        String sql = "SELECT codigo_db, "
                + " codigo_motor, "
                + " general.fn_decifra(nombre_motor_base) AS nombre_motor_base, "
                + " general.fn_decifra(ip_motor_base) AS ip_motor_base, "
                + " general.fn_decifra(nombre_base) AS nombre_base, "
                + " general.fn_decifra(ruta_respaldo_bak) AS ruta_respaldo_bak, "
                + " estado "
                + " FROM controldba.bases "
                + " WHERE estado = 1 "
                + " ORDER BY ip_motor_base";

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RevisionInfra rein = new RevisionInfra();
                rein.setCodigoDb(rs.getString("codigo_db"));
                rein.setCodigoMotor(rs.getString("codigo_motor"));
                rein.setNombreMotorBase(rs.getString("nombre_motor_base"));
                rein.setIpMotorBase(rs.getString("ip_motor_base"));
                rein.setNombreBase(rs.getString("nombre_base"));
                rein.setRutaRespaldoBak(rs.getString("ruta_respaldo_bak"));
                rein.setEstado(rs.getInt("estado"));

                lista.add(rein);
            }
        }

        return lista;
    }
}
