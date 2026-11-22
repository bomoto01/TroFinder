import React, { useState, useEffect, useRef } from 'react';
import { roomService } from '../../services/room.service';
import { aiService } from '../../services/ai.service';
import { MapMarker } from '../../types';
import { MapPin, Sparkles, Loader2, ArrowRight, Search, LocateFixed, RefreshCw } from 'lucide-react';
import { Link } from 'react-router-dom';
import L from 'leaflet';

export const MapViewPage: React.FC = () => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<L.Map | null>(null);
  const markersLayerRef = useRef<L.LayerGroup | null>(null);

  const [markers, setMarkers] = useState<MapMarker[]>([]);
  const [selectedMarker, setSelectedMarker] = useState<MapMarker | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [aiPrompt, setAiPrompt] = useState('');
  const [isAiSearching, setIsAiSearching] = useState(false);
  const [aiResultText, setAiResultText] = useState<string | null>(null);

  const defaultCenter: [number, number] = [21.0285, 105.8542];

  useEffect(() => {
    if (!mapContainerRef.current || mapInstanceRef.current) return;

    const map = L.map(mapContainerRef.current, {
      zoomControl: false,
    }).setView(defaultCenter, 13);
    mapInstanceRef.current = map;

    L.control.zoom({ position: 'bottomright' }).addTo(map);

    // Reliable, free map layers without watermark or API key
    const googleStreetLayer = L.tileLayer('https://mt{s}.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {
      attribution: '&copy; Google Maps',
      subdomains: ['0', '1', '2', '3'],
      maxZoom: 20,
    });

    const googleHybridLayer = L.tileLayer('https://mt{s}.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
      attribution: '&copy; Google Maps',
      subdomains: ['0', '1', '2', '3'],
      maxZoom: 20,
    });

    const esriStreetLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
      attribution: '&copy; Esri &mdash; OpenStreetMap contributors',
      maxZoom: 19,
    });

    // Default to Google Maps Road map
    googleStreetLayer.addTo(map);

    L.control.layers(
      {
        'Google Maps (Đường phố)': googleStreetLayer,
        'Google Maps (Vệ tinh)': googleHybridLayer,
        'Bản đồ Esri Street': esriStreetLayer,
      },
      undefined,
      { position: 'bottomright' }
    ).addTo(map);

    const layerGroup = L.layerGroup().addTo(map);
    markersLayerRef.current = layerGroup;

    // Invalidate map size after DOM mount
    const timer = setTimeout(() => {
      map.invalidateSize();
    }, 200);

    loadMarkers();

    const handleResize = () => map.invalidateSize();
    window.addEventListener('resize', handleResize);

    return () => {
      clearTimeout(timer);
      window.removeEventListener('resize', handleResize);
      map.remove();
      mapInstanceRef.current = null;
    };
  }, []);

  const loadMarkers = async () => {
    setIsLoading(true);
    try {
      const data = await roomService.getMapMarkers();
      const list = data || [];
      setMarkers(list);
      renderMarkers(list);
      if (list.length > 0 && mapInstanceRef.current) {
        fitToMarkers(list);
      }
    } catch (err) {
      console.error('Failed to load map markers:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const fitToMarkers = (markerList: MapMarker[]) => {
    if (!mapInstanceRef.current) return;
    const validPoints = markerList
      .filter((m) => m.latitude && m.longitude)
      .map((m) => [Number(m.latitude), Number(m.longitude)] as [number, number]);

    if (validPoints.length > 0) {
      const bounds = L.latLngBounds(validPoints);
      mapInstanceRef.current.fitBounds(bounds, { padding: [60, 60], maxZoom: 15 });
    }
  };

  const handleAiMapSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!aiPrompt.trim()) return;

    setIsAiSearching(true);
    try {
      const res = await aiService.searchMapByAi(aiPrompt);
      if (res && res.markers) {
        setMarkers(res.markers);
        renderMarkers(res.markers);
        setAiResultText(`AI tìm thấy ${res.totalFound} phòng quanh ${res.anchorName}`);

        if (mapInstanceRef.current && res.centerLat && res.centerLng) {
          mapInstanceRef.current.flyTo(
            [Number(res.centerLat), Number(res.centerLng)],
            res.zoom || 14,
            { duration: 1.5 }
          );
        } else if (res.markers.length > 0) {
          fitToMarkers(res.markers);
        }
      }
    } catch (err) {
      console.error('AI map search failed:', err);
      alert('Không thể tìm kiếm AI trên bản đồ. Vui lòng thử lại.');
    } finally {
      setIsAiSearching(false);
    }
  };

  const renderMarkers = (markerList: MapMarker[]) => {
    if (!mapInstanceRef.current || !markersLayerRef.current) return;

    markersLayerRef.current.clearLayers();

    markerList.forEach((item) => {
      if (item.latitude && item.longitude) {
        const priceMillions = (Number(item.price) / 1000000).toFixed(1).replace('.0', '');
        const customIcon = L.divIcon({
          className: 'custom-map-marker',
          html: `
            <div style="
              background-color: #2563eb;
              color: white;
              border-radius: 9999px;
              padding: 4px 10px;
              font-weight: 700;
              font-size: 12px;
              box-shadow: 0 4px 8px -1px rgba(0,0,0,0.3);
              border: 2px solid white;
              white-space: nowrap;
              cursor: pointer;
              display: flex;
              align-items: center;
              gap: 4px;
            ">
              <span>🏠</span><span>${priceMillions} tr</span>
            </div>
          `,
          iconSize: [68, 28],
          iconAnchor: [34, 14],
        });

        const marker = L.marker([Number(item.latitude), Number(item.longitude)], { icon: customIcon });
        marker.on('click', () => {
          setSelectedMarker(item);
        });
        markersLayerRef.current?.addLayer(marker);
      }
    });
  };

  const getImageSrc = (thumb?: string | null) => {
    if (!thumb) return 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=300&q=80';
    if (thumb.startsWith('http')) return thumb;
    return `http://localhost:8080${thumb}`;
  };

  return (
    <div className="relative h-[calc(100vh-4rem)] flex flex-col">
      {/* Floating AI Map Search Bar */}
      <div className="absolute top-4 left-1/2 -translate-x-1/2 z-[1000] w-full max-w-xl px-4">
        <form
          onSubmit={handleAiMapSearch}
          className="bg-white/95 backdrop-blur-md rounded-2xl shadow-xl border border-indigo-200/80 p-2 flex items-center gap-2"
        >
          <div className="pl-2 text-indigo-500">
            <Sparkles className="w-5 h-5 animate-pulse" />
          </div>
          <input
            type="text"
            value={aiPrompt}
            onChange={(e) => setAiPrompt(e.target.value)}
            placeholder="AI Map: Tìm phòng gần Bách Khoa / ĐH Quốc Gia dưới 4tr..."
            className="flex-1 px-2 py-1.5 text-xs sm:text-sm bg-transparent border-none focus:outline-none text-slate-800 placeholder-slate-400"
          />
          <button
            type="submit"
            disabled={isAiSearching}
            className="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white text-xs font-semibold px-4 py-2 rounded-xl flex items-center gap-1 shrink-0 shadow-sm transition-all"
          >
            {isAiSearching ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Search className="w-3.5 h-3.5" />}
            <span>Tìm bằng AI</span>
          </button>
        </form>

        {aiResultText && (
          <div className="mt-2 text-center">
            <span className="inline-block px-3 py-1 rounded-full bg-slate-900/80 text-white text-xs font-medium backdrop-blur-md shadow-md">
              {aiResultText}
            </span>
          </div>
        )}
      </div>

      {/* Map Header Overlay */}
      <div className="absolute top-4 left-4 z-[999] hidden md:flex bg-white/95 backdrop-blur-md px-4 py-2.5 rounded-2xl shadow-lg border border-slate-200 items-center gap-3">
        <MapPin className="w-5 h-5 text-blue-600" />
        <div>
          <h2 className="text-xs font-bold text-slate-900">Bản đồ trực quan</h2>
          <span className="text-[11px] text-slate-500">Đang ghim {markers.length} phòng trọ</span>
        </div>
        {isLoading && <Loader2 className="w-4 h-4 animate-spin text-blue-600 ml-1" />}
        <button
          onClick={loadMarkers}
          title="Tải lại danh sách phòng"
          className="p-1 text-slate-400 hover:text-blue-600 transition-colors"
        >
          <RefreshCw className="w-3.5 h-3.5" />
        </button>
        {markers.length > 0 && (
          <button
            onClick={() => fitToMarkers(markers)}
            title="Căn chỉnh toàn bộ phòng"
            className="p-1 text-slate-400 hover:text-blue-600 transition-colors"
          >
            <LocateFixed className="w-3.5 h-3.5" />
          </button>
        )}
      </div>

      {/* Map Container */}
      <div
        ref={mapContainerRef}
        style={{ height: 'calc(100vh - 4rem)', width: '100%', minHeight: '500px' }}
        className="w-full flex-1"
      />

      {/* Selected Room Popup Drawer */}
      {selectedMarker && (
        <div className="absolute bottom-6 left-1/2 -translate-x-1/2 z-[1000] max-w-sm w-full mx-auto px-4">
          <div className="bg-white rounded-2xl p-4 shadow-2xl border border-slate-200 flex gap-3 relative">
            <button
              onClick={() => setSelectedMarker(null)}
              className="absolute top-2 right-2 w-6 h-6 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-500 flex items-center justify-center text-xs font-bold"
            >
              ✕
            </button>
            <div className="w-20 h-20 rounded-xl bg-slate-100 overflow-hidden shrink-0">
              <img
                src={getImageSrc(selectedMarker.thumbnail)}
                alt={selectedMarker.title}
                className="w-full h-full object-cover"
              />
            </div>
            <div className="flex-1 flex flex-col justify-between">
              <div>
                <span className="text-sm font-bold text-red-600 block">
                  {(Number(selectedMarker.price) / 1000000).toLocaleString('vi-VN')} tr/tháng
                </span>
                <h4 className="text-xs font-semibold text-slate-900 line-clamp-1">{selectedMarker.title}</h4>
                <p className="text-[11px] text-slate-500 line-clamp-1">{selectedMarker.address}</p>
              </div>
              <Link
                to={`/rooms/${selectedMarker.id}`}
                className="text-xs font-semibold text-blue-600 hover:text-blue-700 flex items-center gap-1 mt-1"
              >
                Xem chi tiết phòng <ArrowRight className="w-3.5 h-3.5" />
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
